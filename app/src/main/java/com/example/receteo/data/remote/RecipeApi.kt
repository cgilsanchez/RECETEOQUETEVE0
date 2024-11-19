package com.example.receteo.data.remote

import com.example.receteo.data.local.RecipeEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface RecipeApi {
    @GET("recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @POST("recipes")
    suspend fun createRecipe(@Body recipe: RecipeEntity)

    @PUT("recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: Int, @Body recipe: RecipeEntity)

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int)
}