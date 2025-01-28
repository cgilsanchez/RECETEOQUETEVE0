package com.example.receteo.data.remote.models



data class UserModel(
    val jwt: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val email: String
)


