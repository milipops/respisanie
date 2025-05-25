package com.example.lessons.Methods

import java.security.MessageDigest

object DefaultPasswordHasher : PasswordHasher {
    override fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}

interface PasswordHasher {
    fun hashPassword(password: String): String
}


