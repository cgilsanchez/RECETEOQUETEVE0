package com.example.receteo.data.remote

import com.example.receteo.data.local.RecipeEntity
import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recipes")
    suspend fun getAllRecipes(): Response<List<RecipeEntity>>

    @POST("recipes")
    suspend fun createRecipe(@Body recipe: RecipeEntity): Response<RecipeEntity>

    @PUT("recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") id: Int,
        @Body recipe: RecipeEntity
    ): Response<RecipeEntity>

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>
}
