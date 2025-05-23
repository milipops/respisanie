package com.example.lessons.ui.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessons.Models.Lesson
import com.example.lessons.Models.Person
import com.example.lessons.Models.ScheduleItem
import com.example.lessons.Models.ScheduleResponse
import com.example.lessons.Models.UserManager
import com.example.lessons.Models.group
import com.example.lessons.supabase
import com.google.gson.Gson
import com.example.lessons.supabase
import com.google.android.gms.auth.api.signin.internal.Storage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Bucket
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.client.call.NoTransformationFoundException
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID

class WorkForPerson(
) : ViewModel() {

    val _currentUser = MutableStateFlow<List<Person>>(emptyList())
    val currentUser: StateFlow<List<Person>> get() = _currentUser
    val currentUser2 by UserManager.currentUser

    fun uploadAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                // Превращаем картинку в байты
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("Не получилось открыть картинку")

                // Создаем уникальное имя файла
                val userId = currentUser2?.id
                    ?: throw Exception("Пользователь не вошел в систему")
                val fileName = "avatar_$userId.jpg"

                // Загружаем в Storage (используем ByteArray)
                supabase.storage.from("avatars")
                    .upload(
                        path = fileName,
                        data = bytes,
                        upsert = true // перезапись
                    )

                // Получаем публичный URL
                val imageUrl = supabase.storage
                    .from("avatars")
                    .publicUrl(fileName)

                // Обновляем в базу
                supabase.from("person")
                    .update({
                        set("image_url", imageUrl)
                    }) {
                        filter {
                            eq("id", userId)
                        }
                    }

                Toast.makeText(context, "Аватар обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("UploadAvatar", "Ошибка загрузки", e)
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // расписание
    private val _scheduleState = MutableStateFlow<List<ScheduleItem>>(emptyList())
    val scheduleState: StateFlow<List<ScheduleItem>> = _scheduleState

    fun loadSchedule(groupId: Int, epochDate: Long) {
        viewModelScope.launch {
            try {
                val url =
                    "https://api.it-reshalo.ru/schedule?filter_id=$groupId&date=$epochDate&regarding=group"

                val responseText = withContext(Dispatchers.IO) {
                    URL(url).readText()
                }

                val scheduleResponse = Gson().fromJson(responseText, ScheduleResponse::class.java)
                val main = scheduleResponse.result?.main.orEmpty()
                val changes = scheduleResponse.result?.change.orEmpty()

                val merged = mergeSchedule(main, changes)

                _scheduleState.value = merged

                Log.d("ScheduleDebug", "JSON-ответ:\n$responseText")

            } catch (e: Exception) {
                println("Ошибка загрузки расписания: ${e.message}")
            }
        }
    }

    fun mergeSchedule(
        main: List<ScheduleItem>?,
        change: List<ScheduleItem>?,
    ): List<ScheduleItem> {
        val result = mutableListOf<ScheduleItem>()

        // Добавляем все основные пары (type = "main")
        main?.forEach { item ->
            result.add(item.copy(type = "main"))
        }

        // Добавляем все изменения (type = "change")
        change?.forEach { changedItem ->
            val isNote = changedItem.paraTo == null

            val newItem = if (isNote) {
                ScheduleItem(
                    id = changedItem.id,
                    para = changedItem.para,
                    lesson = Lesson(
                        id = 0,
                        name = "",
                        instrumental_case = ""
                    ),
                    teachers = emptyList(),
                    cabinet = null,
                    group = null,
                    type = "change",
                    paraFrom = null,
                    paraTo = null,
                    noteData = changedItem.noteData,
                    parallel = changedItem.parallel
                )
            } else {
                // Это замена пары
                ScheduleItem(
                    id = changedItem.id,
                    para = changedItem.para,
                    lesson = changedItem.paraTo?.lesson,
                    teachers = changedItem.paraTo?.teachers,
                    cabinet = changedItem.cabinet,
                    group = changedItem.group,
                    type = "change",
                    paraFrom = changedItem.paraFrom,
                    paraTo = changedItem.paraTo,
                    noteData = changedItem.noteData,
                    parallel = changedItem.parallel
                )
            }

            result.add(newItem)
        }

        // Сортируем сначала по номеру пары, потом по type ("main" перед "change")
        return result.sortedWith(
            compareBy<ScheduleItem> { it.para }.thenBy {
                when {
                    "main" in it.type -> 0
                    else -> 1
                }
            }
        )
    }


    suspend fun getUrlImage(cardName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = supabase.storage.from("avatars").publicUrl("${cardName}.png")
                Log.d("buck", url)
                url
            } catch (e: Exception) {
                Log.e("getUrlImage", "Error getting image URL", e)
                ""
            }

        }
    }

    val _group = MutableStateFlow<List<group>>(emptyList())
    val Group: StateFlow<List<group>> get() = _group

    init {
        loadGroup()

    }

    fun loadGroup() {
        viewModelScope.launch {
            try {
                val loadedGroups = supabase.postgrest.from("Group").select().decodeList<group>()
                if (loadedGroups.isNotEmpty()) {
                    _group.value = loadedGroups
                    Log.d("MainGroup", "Загружено групп: ${loadedGroups.size}")
                } else {
                    Log.d("MainGroup", "Группы не найдены")
                }
            } catch (e: Exception) {
                Log.e("MainGroup", "Ошибка загрузки групп: ${e.message}")
            } catch (ex: Exception) {
                Log.e("MainGroup", "Неизвестная ошибка: ${ex.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.clearSession()
                _currentUser.value = emptyList()
                UserManager.clearUser()
            } catch (e: Exception) {
                Log.e("Auth", "Ошибка при выходе", e)
            }
        }
    }

     fun deleteAccount(userId: String, context: Context, onResult: () -> Unit) {
        viewModelScope.launch {
            try {
                // Пример: удаление из репозитория
                supabase.from("person").delete{
                        filter {
                            eq("id",userId)
                        }
                    }

                // Очистка данных пользователя
                UserManager.clearUser()
                onResult()
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка при удалении: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}