package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.UserModel
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface AuthApi {
    @POST("api/auth/local")
    suspend fun login(@Body credentials: Map<String, String>): Response<UserModel>

    @POST("api/auth/local/register")
    suspend fun register(@Body user: Map<String, String>): Response<UserModel>
}