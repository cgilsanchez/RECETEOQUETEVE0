package com.example.receteo.data.remote



import com.example.receteo.data.remote.models.ChefModel
import retrofit2.Response
import retrofit2.http.*

interface ChefApi {

    @GET("chefs")
    suspend fun getAllChefs(): Response<List<ChefModel>>

    @POST("chefs")
    suspend fun createChef(@Body chef: ChefModel): Response<ChefModel>

    @PUT("chefs/{id}")
    suspend fun updateChef(@Path("id") id: Int, @Body chef: ChefModel): Response<ChefModel>

    @DELETE("chefs/{id}")
    suspend fun deleteChef(@Path("id") id: Int): Response<Unit>
}
