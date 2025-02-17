package com.example.receteo.di

import com.example.receteo.data.remote.AuthApi
import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.RecipeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://people-service-6bwq.onrender.com/api/" // Asegúrate de usar la URL correcta de la API

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Tiempo de conexión
            .readTimeout(60, TimeUnit.SECONDS)     // Tiempo de lectura
            .writeTimeout(60, TimeUnit.SECONDS)    // Tiempo de escritura
            .build()
    }



    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    fun provideChefApi(retrofit: Retrofit): ChefApi = retrofit.create(ChefApi::class.java)

    @Provides
    fun provideRecipeApi(retrofit: Retrofit): RecipeApi = retrofit.create(RecipeApi::class.java)
}
