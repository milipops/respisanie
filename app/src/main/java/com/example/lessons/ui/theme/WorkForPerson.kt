package com.example.lessons.ui.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessons.supabase
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.client.call.NoTransformationFoundException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
}