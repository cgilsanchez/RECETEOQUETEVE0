package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody

import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
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
                        chef = recipeData.attributes.chef?.data?.id ?: 0,
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
                response.body()?.data?.let { recipeData ->
                    RecipeModel(
                        id = recipeData.id,
                        name = recipeData.attributes.name,
                        descriptions = recipeData.attributes.descriptions,
                        ingredients = recipeData.attributes.ingredients,
                        chef = recipeData.attributes.chef?.data?.id ?: 0, // ✅ Ahora trae el chef correctamente
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "", // ✅ Ahora trae la imagen correctamente
                        isFavorite = recipeData.attributes.isFavorite ?: false
                    )
                }
            } else {
                Log.e("RecipeRepository", "❌ Error en la API: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "❌ Excepción en getRecipeById: ${e.message}")
            null
        }
    }







    suspend fun createRecipe(recipeRequest: RecipeRequestModel, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO + SupervisorJob()) {
            try {
                Log.d("RecipeRepository", "📤 Creando receta...")

                val imageId = imageFile?.let { uploadImage(it) }
                if (imageId == null && imageFile != null) {
                    Log.e("RecipeRepository", "❌ No se pudo subir la imagen")
                    return@withContext false
                }

                val updatedRequest = recipeRequest.copy(
                    data = recipeRequest.data.copy(image = imageId?.let { listOf(it) } ?: emptyList())
                )

                val jsonBody = Gson().toJson(updatedRequest).toRequestBody("application/json".toMediaTypeOrNull())

                Log.d("RecipeRepository", "📤 Enviando request de creación de receta: $jsonBody")

                val response = api.createRecipe(jsonBody)

                if (response.isSuccessful) {
                    Log.d("RecipeRepository", "✅ Receta creada con éxito")
                    true
                } else {
                    Log.e("RecipeRepository", "❌ Error en la API al crear receta: ${response.errorBody()?.string()}")
                    false
                }
            } catch (e: CancellationException) {
                Log.e("RecipeRepository", "❌ Corrutina cancelada en createRecipe: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e("RecipeRepository", "❌ Excepción en createRecipe: ${e.message}")
                false
            }
        }
    }


    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("RecipeRepository", "📤 Iniciando actualización de receta...")

                // Subimos la imagen si hay una nueva
                val imageId = imageFile?.let { uploadImage(it) }

                // Ajustamos el JSON correctamente para Strapi
                val updatedRequest = recipeRequest.copy(
                    data = recipeRequest.data.copy(
                        image = imageId?.let { listOf(it) } ?: recipeRequest.data.image,
                        chef = recipeRequest.data.chef
                    )
                )

                // Convertir el objeto en JSON de forma correcta
                val jsonBody = Gson().toJson(updatedRequest)
                Log.d("RecipeRepository", "📤 JSON Enviado: $jsonBody")

                // Enviar la petición con el JSON bien formateado
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
                val response = api.updateRecipe(recipeId, requestBody)

                if (response.isSuccessful) {
                    Log.d("RecipeRepository", "✅ Receta actualizada con éxito")
                    true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.e("RecipeRepository", "❌ Error en la API al actualizar receta: $errorResponse")
                    false
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", "❌ Excepción en updateRecipe: ${e.message}")
                false
            }
        }
    }




    suspend fun uploadImage(imageFile: File): Int? {
        return withContext(Dispatchers.IO + SupervisorJob()) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("files", imageFile.name, requestFile)

                Log.d("RecipeRepository", "📤 Intentando subir imagen: ${imageFile.name}")

                val response = api.uploadImage(body)

                if (response.isSuccessful) {
                    val uploadedId = response.body()?.firstOrNull()?.id
                    Log.d("RecipeRepository", "✅ Imagen subida con éxito. ID: $uploadedId")
                    uploadedId
                } else {
                    Log.e("RecipeRepository", "❌ Error en la subida de imagen: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: SocketTimeoutException) {
                Log.e("RecipeRepository", "❌ Timeout en uploadImage: Reintentando...")
                delay(2000)  // Espera 2 segundos antes de reintentar
                uploadImage(imageFile)  // Reintento automático
            } catch (e: IOException) {
                Log.e("RecipeRepository", "❌ Error de red en uploadImage: ${e.message}")
                null
            } catch (e: Exception) {
                Log.e("RecipeRepository", "❌ Excepción en uploadImage: ${e.message}")
                null
            }
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
