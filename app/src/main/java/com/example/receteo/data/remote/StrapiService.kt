package com.example.receteo.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
interface StrapiService {


    object ApiClient {
        private const val BASE_URL = "https://people-service-6bwq.onrender.com/api/"

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}