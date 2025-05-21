package com.example.lessons.ui.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< HEAD
import com.example.lessons.Models.Lesson
import com.example.lessons.Models.ScheduleItem
import com.example.lessons.Models.ScheduleResponse
import com.example.lessons.supabase
import com.google.gson.Gson
=======
import com.example.lessons.supabase
>>>>>>> a7ebd75cae2dbe5d4ba9bdb7345162a4964e27ea
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.client.call.NoTransformationFoundException
<<<<<<< HEAD
import kotlinx.coroutines.Dispatchers
=======
>>>>>>> a7ebd75cae2dbe5d4ba9bdb7345162a4964e27ea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
<<<<<<< HEAD
import kotlinx.coroutines.withContext
import java.net.URL
=======
>>>>>>> a7ebd75cae2dbe5d4ba9bdb7345162a4964e27ea
import java.util.UUID

class WorkForPerson :ViewModel() {

    val _currentUser = MutableStateFlow<List<com.example.lessons.Models.Person>>(emptyList())
    val currentUser: StateFlow<List<com.example.lessons.Models.Person>> get() = _currentUser

    fun uploadAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                // Превращаем картинку в байты
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("Не получилось открыть картинку")

                // Создаем уникальное имя файла
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Пользователь не вошел в систему")
                val fileName = "avatar_$userId.jpg"

                // Загружаем в Storage (используем ByteArray)
                supabase.storage.from("avatars")
                    .upload(path = fileName,
                        data = bytes,
                            upsert = true // перезапись
                        )

                // Получаем публичный URL
                val imageUrl = supabase.storage
                    .from("avatars")
                    .publicUrl(fileName)

                // Обновляем в базу
                supabase.from("Users")
                    .update({
                        set("Image",imageUrl)
                    }){
                        filter {
                            eq("id",userId)
                        }
                    }

                Toast.makeText(context, "Аватар обновлен", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("UploadAvatar", "Ошибка загрузки", e)
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
<<<<<<< HEAD

    private val _scheduleState = MutableStateFlow<List<ScheduleItem>>(emptyList())
    val scheduleState: StateFlow<List<ScheduleItem>> = _scheduleState

    fun loadSchedule(groupId: Int, epochDate: Long) {
        viewModelScope.launch {
            try {
                val url = "https://api.it-reshalo.ru/schedule?filter_id=$groupId&date=$epochDate&regarding=group"

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

    private fun mergeSchedule(
        main: List<ScheduleItem>?,
        change: List<ScheduleItem>?
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
                // Просто заметка без замены
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
=======
>>>>>>> a7ebd75cae2dbe5d4ba9bdb7345162a4964e27ea
}