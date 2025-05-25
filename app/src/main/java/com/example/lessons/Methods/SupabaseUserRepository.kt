package com.example.lessons.Methods

import android.util.Log
import com.example.lessons.Models.Person
import com.example.lessons.supabase
import io.github.jan.supabase.postgrest.from

class SupabaseUserRepository : UserRepository {
    override suspend fun findUserByEmail(email: String): Person? {
        return try {
            supabase.from("person")
                .select()
                .decodeList<Person>()
                .find { it.email == email.trim() }
        } catch (e: Exception) {
            Log.e("SupabaseUserRepository", "Error finding user", e)
            null
        }
    }
}


interface UserRepository {
    suspend fun findUserByEmail(email: String): Person?
}