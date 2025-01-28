package com.example.receteo.data.remote.models





data class AuthRequest(
    val identifier: String, // Email o username
    val password: String
)
