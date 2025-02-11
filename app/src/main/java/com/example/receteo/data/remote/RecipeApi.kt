package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.RecipeResponse
import com.example.receteo.data.remote.models.RecipeRequestModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recetas?populate=image")
    suspend fun getRecipes(): Response<RecipeResponse>

    @GET("recetas/{id}")
    suspend fun getRecipeById(@Path("id") id: Int): Response<RecipeResponse>

    @Multipart
    @POST("recetas")
    suspend fun createRecipe(
        @Part("data") recipe: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<RecipeResponse>

    @Multipart
    @PUT("recetas/{id}")
    suspend fun updateRecipe(
        @Path("id") recipeId: Int,
        @Part("data") recipe: RecipeRequestModel,
        @Part image: MultipartBody.Part?
    ): Response<RecipeResponse>


    @DELETE("recetas/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>
}
