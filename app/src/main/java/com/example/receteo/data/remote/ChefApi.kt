package com.example.receteo.data.remote



import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.ChefRequestModel
import com.example.receteo.data.remote.models.ChefResponse
import retrofit2.Response
import retrofit2.http.*

interface ChefApi {

    @GET("chefs")
    suspend fun getChefs(): Response<ChefResponse>

    @GET("chefs/{id}")
    suspend fun getChefById(@Path("id") id: Int): Response<ChefResponse>

    @POST("chefs")
    suspend fun createChef(@Body chefRequest: ChefRequestModel): Response<ChefResponse>

    @PUT("chefs/{id}")
    suspend fun updateChef(@Path("id") id: Int, @Body chefRequest: ChefRequestModel): Response<ChefResponse>

    @DELETE("chefs/{id}")
    suspend fun deleteChef(@Path("id") id: Int): Response<Unit>
}
