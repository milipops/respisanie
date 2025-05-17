package com.example.lessons.Screens.Login

import android.util.Log
import androidx.navigation.NavController
import com.example.lessons.Methods.isEmailValid
import com.example.lessons.Models.Person
import com.example.lessons.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginModel {

    suspend fun findUserByEmail(email: String): Person? {
        Log.d("LoginModel", "Поиск пользователя по email: $email")
        return try {
            val response = supabase.from("person")
                .select()
            Log.d("LoginModel", "Ответ от Supabase: $response")

            val users = response.decodeList<Person>()
            Log.d("LoginModel", "Пользователи из базы: ${users.size}")

            users.find { it.email == email.trim() }
        } catch (e: Exception) {
            Log.e("LoginModel", "Ошибка поиска пользователя", e)
            null
        }
    }
    fun handleLogin(
        email: String,
        password: String,
        navController: NavController,
        coroutineScope: CoroutineScope,
        onError: (String) -> Unit,
        onLoading: (Boolean) -> Unit,
        onSuccess: (Person) -> Unit
    ) {
        if (email.isBlank() || !email.isEmailValid()) {
            Log.d("LoginModel", "Некорректный email: $email")
            onError("Введите корректный email")
            return
        }

        onLoading(true)

        coroutineScope.launch {
            try {
                Log.d("LoginModel", "Попытка найти пользователя по email: $email")
                val user = findUserByEmail(email.trim())

                if (user == null) {
                    Log.d("LoginModel", "Пользователь не найден по email: $email")
                    onError("Пользователь не найден")
                } else {
                    Log.d("LoginModel", "Пользователь найден: ${user.email}")

                    if (user.password != password) {
                        Log.d("LoginModel", "Неверный пароль для email: $email")
                        onError("Неверный пароль")
                    } else {
                        Log.d("LoginModel", "Успешный вход пользователя с email: ${user.email}")
                        onSuccess(user)
                        navController.navigate("content") { popUpTo("login") }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginModel", "Ошибка при входе", e)
            } finally {
                onLoading(false)
            }
        }
    }
}




