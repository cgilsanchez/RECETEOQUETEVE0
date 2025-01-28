package com.example.receteo.data.remote.models

data class UserModel(
    val id: Int,
    val username: String,
    val email: String,
    val jwt: String? // Token para autenticación
)