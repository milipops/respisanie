package com.example.lessons.Methods

fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// для тестов
//fun String.isEmailValid(): Boolean {
//    if (this.isEmpty()) return false
//
//    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
//    return emailRegex.toRegex().matches(this)
//}