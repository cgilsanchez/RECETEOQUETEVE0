package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.AuthRequest
import com.example.receteo.data.remote.models.RegisterRequest
import com.example.receteo.data.remote.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/local") // URL corregida
    suspend fun login(@Body request: AuthRequest): Response<UserModel>

    @POST("api/auth/local/register") // URL corregida
    suspend fun register(@Body request: RegisterRequest): Response<UserModel>
}
