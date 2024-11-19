package com.example.receteo.data.Api

import com.example.receteo.data.db.RecipeEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body


interface RecipeApi {
    @GET("recipes")
    suspend fun getRecipes(): List<RecipeEntity>

    @POST("recipes")
    suspend fun createRecipe(@Body recipe: RecipeEntity): RecipeEntity
}