package com.example.receteo.data.repository

import com.example.receteo.data.remote.AuthApi
import com.example.receteo.data.remote.models.AuthRequest
import com.example.receteo.data.remote.models.RegisterRequest
import com.example.receteo.data.remote.models.User
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
                android.util.Log.e("AuthRepository", "Error en login: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Excepción en login: ${e.message}")
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
            android.util.Log.e("AuthRepository", "Excepción en registro: ${e.message}")
            null
        }
    }

    suspend fun getUserData(token: String): User? {
        return try {
            val response: Response<User> = authApi.getUserData("Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Excepción al obtener usuario: ${e.message}")
            null
        }
    }
}

