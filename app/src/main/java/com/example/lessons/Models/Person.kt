package com.example.lessons.Models

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: String,
    val email: String,
    val name: String,
    val password: String,
    val phone: String,
    val image_url: String? = null
)

