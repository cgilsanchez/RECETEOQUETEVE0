package com.example.receteo.data.remote

import com.example.receteo.data.local.RecipeEntity
import com.example.receteo.data.remote.models.RecipeModel
import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recetas")
    suspend fun getAllRecipes(): Response<List<RecipeModel>>

    @POST("recetas")
    suspend fun createRecipe(@Body recipe: RecipeModel): Response<RecipeModel>

    @PUT("recetas/{id}")
    suspend fun updateRecipe(@Path("id") id: Int, @Body recipe: RecipeModel): Response<RecipeModel>

    @DELETE("recetas/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>
}