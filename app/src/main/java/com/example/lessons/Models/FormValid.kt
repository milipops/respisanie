package com.example.lessons.Models

object FormValid {
    /**
     * Проверяет валидность email адреса
     * @param email Строка для проверки
     * @return true если email валиден
     */
    fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

    /**
     * Проверяет валидность пароля
     * @param password Пароль для проверки
     * @param minLength Минимальная длина пароля (по умолчанию 8)
     * @return true если пароль валиден
     */
    fun validatePassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }
}
