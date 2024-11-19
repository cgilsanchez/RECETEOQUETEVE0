package com.example.receteo.data.Api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import com.example.recipeproject.data.db.RecipeEntity

interface RecipeApi {
    @GET("recipes")
    suspend fun getRecipes(): List<RecipeEntity>

    @POST("recipes")
    suspend fun createRecipe(@Body recipe: RecipeEntity): RecipeEntity
}