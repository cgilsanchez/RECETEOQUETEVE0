package com.example.receteo.data.remote

import com.example.receteo.data.remote.models.RecipeRespons
import com.example.receteo.data.remote.models.RecipeResponse
import com.example.receteo.data.remote.models.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RecipeApi {

    @GET("recetas?populate=image")
    suspend fun getRecipes(): Response<RecipeResponse>

    @GET("recetas/{id}?populate=image,chef")
    suspend fun getRecipeById(@Path("id") id: Int): Response<RecipeRespons>


    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<List<UploadResponse>>

    @POST("recetas")
    suspend fun createRecipe(
        @Body recipe: RequestBody
    ): Response<RecipeResponse>

    @PUT("recetas/{id}")
    suspend fun updateRecipe(
        @Path("id") recipeId: Int,
        @Body recipe: RequestBody
    ): Response<RecipeResponse>

    @DELETE("recetas/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>
}
