package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody

import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RecipeRepository @Inject constructor(private val api: RecipeApi) {

    suspend fun getRecipes(): List<RecipeModel> {
        return try {
            val response = api.getRecipes()
            if (response.isSuccessful) {
                response.body()?.data?.map { recipeData ->
                    RecipeModel(
                        id = recipeData.id,
                        name = recipeData.attributes.name,
                        descriptions = recipeData.attributes.descriptions,
                        ingredients = recipeData.attributes.ingredients,
                        chef = recipeData.attributes.chef,
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "",
                        isFavorite = recipeData.attributes.isFavorite ?: false
                    )
                } ?: emptyList()
            } else {
                Log.e("RecipeRepository", "Error en la API: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Excepción obteniendo recetas: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRecipeById(recipeId: Int): RecipeModel? {
        return try {
            val response = api.getRecipeById(recipeId)
            if (response.isSuccessful) {
                response.body()?.data?.firstOrNull()?.let { recipeData ->
                    RecipeModel(
                        id = recipeData.id,
                        name = recipeData.attributes.name,
                        descriptions = recipeData.attributes.descriptions,
                        ingredients = recipeData.attributes.ingredients,
                        chef = recipeData.attributes.chef,
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "",
                        isFavorite = recipeData.attributes.isFavorite ?: false
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error al obtener receta: ${e.message}")
            null
        }
    }


    suspend fun createRecipe(recipeRequest: RecipeRequestModel, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("RecipeRepository", "📤 Enviando solicitud para crear receta...")

                // 🔹 Enviar JSON correctamente formateado con la estructura esperada por Strapi
                val jsonMap = mapOf("data" to recipeRequest)
                val jsonBody = Gson().toJson(jsonMap)
                    .toRequestBody("application/json".toMediaTypeOrNull())

                // 🔹 Preparar la imagen (si existe)
                val imagePart = imageFile?.let {
                    MultipartBody.Part.createFormData(
                        "files.image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                Log.d("RecipeRepository", "⏳ Enviando petición a Strapi...")

                val response = api.createRecipe(jsonBody, imagePart)

                if (response.isSuccessful) {
                    Log.d("RecipeRepository", "✅ Receta creada correctamente en Strapi")
                    return@withContext true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RecipeRepository", "❌ Error en la API: $errorBody")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", "🚨 Excepción al crear receta: ${e.localizedMessage}")
                return@withContext false
            }
        }
    }





    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?): Boolean {
        return try {
            val imagePart = imageFile?.toMultipartBody()
            val response = api.updateRecipe(recipeId, recipeRequest, imagePart)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error al actualizar receta: ${e.message}")
            false
        }
    }


    suspend fun deleteRecipe(recipeId: Int): Boolean {
        return try {
            val response = api.deleteRecipe(recipeId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Excepción eliminando receta: ${e.message}")
            false
        }
    }

    // 🟢 Convierte RecipeRequestModel en RequestBody para enviarlo como JSON
    private fun RecipeRequestModel.toJsonRequestBody(): RequestBody {
        val json = Gson().toJson(this)
        return json.toRequestBody("application/json".toMediaType())
    }

    // 🟢 Convierte un archivo en MultipartBody.Part para enviar imágenes
    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestFile = this.asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData("files.image", this.name, requestFile)
    }
}
