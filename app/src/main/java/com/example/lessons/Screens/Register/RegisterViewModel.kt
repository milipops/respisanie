package com.example.lessons.Screens.Register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessons.Methods.DefaultPasswordHasher.hashPassword
import com.example.lessons.Methods.isEmailValid
import com.example.lessons.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class RegisterViewModel : ViewModel() {
    private val _uiState = mutableStateOf(RegisterState())
    val uiState: State<RegisterState> = _uiState

    private val _resultState = MutableStateFlow<ResultState>(ResultState.Initialized)
    val resultState: StateFlow<ResultState> = _resultState.asStateFlow()

    fun updateState(newState: RegisterState) {
        _uiState.value = newState.copy(
            isEmailValid = newState.email.isEmailValid()
        )
        _resultState.value = ResultState.Initialized
    }


    fun isPhoneValid(phone: String): Boolean {
        return phone.length == 11 && phone.startsWith("7")
    }



    fun register() {
        println("Начало регистрации...")

        if (!_uiState.value.isEmailValid) {
            _resultState.value = ResultState.Error("Некорректный email")
            return
        }

        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _resultState.value = ResultState.Error("Пароли не совпадают")
            return
        }

        if (_uiState.value.password.length < 6) {
            _resultState.value = ResultState.Error("Пароль должен быть не менее 6 символов")
            return
        }

        if (!isPhoneValid(_uiState.value.phone)) {
            _resultState.value = ResultState.Error("Некорректный номер телефона")
            return
        }


        _resultState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                println("Отправка данных в Supabase...")

                val hashedPassword = hashPassword(_uiState.value.password)

                val userData = mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "name" to _uiState.value.name,
                    "email" to _uiState.value.email,
                    "password" to hashedPassword,
                    "phone" to "+${_uiState.value.phone}"
                )


                supabase.from("person").insert(userData)

                _resultState.value = ResultState.Success("Регистрация успешна!")
            } catch (ex: Exception) {
                println("Ошибка: ${ex.stackTraceToString()}")
                _resultState.value = ResultState.Error("Ошибка: ${ex.message}")
            }
        }
    }
}


data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val isEmailValid: Boolean = true
)

sealed class ResultState {
    object Initialized : ResultState()
    object Loading : ResultState()
    data class Success(val message: String) : ResultState()
    data class Error(val message: String) : ResultState()
}