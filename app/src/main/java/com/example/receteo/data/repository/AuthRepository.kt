package com.example.receteo.data.repository

import com.example.receteo.data.remote.AuthApi
import com.example.receteo.data.remote.models.UserModel
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApi: AuthApi) {

    suspend fun login(username: String, password: String): UserModel? {
        val response = authApi.login(mapOf("identifier" to username, "password" to password))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun register(username: String, email: String, password: String): UserModel? {
        val response = authApi.register(mapOf("username" to username, "email" to email, "password" to password))
        return if (response.isSuccessful) response.body() else null
    }
}