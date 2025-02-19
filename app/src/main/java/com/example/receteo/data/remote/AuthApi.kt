package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.AuthRequest
import com.example.receteo.data.remote.models.RegisterRequest
import com.example.receteo.data.remote.models.User
import com.example.receteo.data.remote.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/local")
    suspend fun login(@Body request: AuthRequest): Response<UserModel>

    @POST("auth/local/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserModel>


    @GET("users/me")
    suspend fun getUserData(@Header("Authorization") token: String): Response<User>

}
