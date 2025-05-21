package com.example.lessons.Models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object UserManager {
    private var _currentUser = mutableStateOf<Person?>(null)
    val currentUser: State<Person?> = _currentUser

    fun setUser(user: Person) {
        _currentUser.value = user
    }

    fun clearUser() {
        _currentUser.value = null
    }
}
