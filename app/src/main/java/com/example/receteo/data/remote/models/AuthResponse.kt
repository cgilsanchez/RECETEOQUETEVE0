package com.example.receteo.data.remote.models

data class AuthResponse(
    val jwt: String,
    val user: UserModel
)