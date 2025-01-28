package com.example.receteo.data.repository

import com.example.receteo.data.remote.AuthApi
import com.example.receteo.data.remote.models.AuthRequest
import com.example.receteo.data.remote.models.RegisterRequest
import com.example.receteo.data.remote.models.UserModel
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun login(identifier: String, password: String): UserModel? {
        return try {
            val response: Response<UserModel> = authApi.login(AuthRequest(identifier, password))
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(username: String, email: String, password: String): UserModel? {
        return try {
            val response: Response<UserModel> = authApi.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
