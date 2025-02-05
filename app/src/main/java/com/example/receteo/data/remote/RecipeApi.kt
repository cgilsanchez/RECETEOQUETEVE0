package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.RecipeData
import com.example.receteo.data.remote.models.RecipeResponse
import com.example.receteo.data.remote.models.RecipeRequestModel
import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recetas?populate=image")
    suspend fun getRecipes(): Response<RecipeResponse>

    @GET("recetas/{id}")
    suspend fun getRecipeById(@Path("id") id: Int): Response<RecipeResponse>

    @POST("recetas")
    suspend fun createRecipe(@Body recipe: RecipeRequestModel): Response<RecipeResponse>


    @PUT("recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") recipeId: Int,
        @Body requestBody: Map<String, Any> // Corregido para permitir datos dinámicos
    ): Response<RecipeResponse>


    @DELETE("recetas/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>
}
