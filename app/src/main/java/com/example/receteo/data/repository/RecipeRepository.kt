package com.example.receteo.data.repository

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import com.example.receteo.ui.notification.RecipeWorker
import com.google.gson.Gson
import dagger.hilt.android.internal.Contexts.getApplication
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RecipeRepository @Inject constructor(private val api: RecipeApi,private val context: Context) {

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
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "", // ✅ Siempre obtenemos la imagen correcta
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

                // Intentar subir imagen si existe
                val imageId = imageFile?.let { uploadImage(it) }
                if (imageId == null && imageFile != null) {
                    Log.e("RecipeRepository", "❌ No se pudo subir la imagen")
                    return@withContext false
                }

                // 🔥 Asegurar que `image` tenga el formato correcto `List<Map<String, Int>>`
                val imageList = mutableListOf<Map<String, Int>>()
                imageId?.let { imageList.add(mapOf("id" to it)) } // ✅ Si hay nueva imagen, agregar

                val updatedRequest = mapOf(
                    "data" to mapOf(
                        "chef" to recipeRequest.data.chef,
                        "descriptions" to recipeRequest.data.descriptions,
                        "image" to imageList, // ✅ Siempre en formato `List<Map<String, Int>>`
                        "ingredients" to recipeRequest.data.ingredients,
                        "isFavorite" to recipeRequest.data.isFavorite,
                        "name" to recipeRequest.data.name
                    )
                )

                val jsonBody = Gson().toJson(updatedRequest).toRequestBody("application/json".toMediaTypeOrNull())
                Log.d("RecipeRepository", "📤 JSON Enviado: $jsonBody")

                val response = api.createRecipe(jsonBody)

                return@withContext if (response.isSuccessful) {
                    Log.d("RecipeRepository", "✅ Receta creada con éxito")
                    triggerNotification("Receta creada con éxito") // 🔥 Activar notificación después de la creación
                    true
                } else {
                    Log.e("RecipeRepository", "❌ Error en la API: ${response.errorBody()?.string()}")
                    false
                }

            } catch (e: Exception) {
                Log.e("RecipeRepository", "⚠️ Excepción en createRecipe: ${e.message}")
                triggerNotification("create")
                false
            }
        }
    }


    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("RecipeRepository", "📤 Iniciando actualización de receta en Strapi...")

                // ✅ Obtener la receta actual antes de actualizar
                val existingRecipe = getRecipeById(recipeId)
                val previousImageId = existingRecipe?.imageUrl?.takeIf { it.isNotBlank() }

                // 🔥 Si el usuario no seleccionó una nueva imagen, mantener la anterior
                val imageId = imageFile?.let { uploadImage(it) } ?: previousImageId

                // 🔥 Asegurar que `image` siempre sea un array correcto para Strapi
                val imageList = if (imageId != null) {
                    listOf(mapOf("id" to imageId.toString().toInt()))
                } else {
                    emptyList()
                }


                val updatedRequest = mapOf(
                    "data" to mapOf(
                        "chef" to recipeRequest.data.chef,
                        "descriptions" to recipeRequest.data.descriptions,
                        "image" to imageList, // ✅ Strapi siempre recibe un array correcto
                        "ingredients" to recipeRequest.data.ingredients,
                        "isFavorite" to recipeRequest.data.isFavorite,
                        "name" to recipeRequest.data.name
                    )
                )

                val jsonBody = Gson().toJson(updatedRequest).toRequestBody("application/json".toMediaTypeOrNull())
                Log.d("RecipeRepository", "📤 JSON Enviado: $jsonBody")

                val response = api.updateRecipe(recipeId, jsonBody)
                return@withContext if (response.isSuccessful) {
                    Log.d("RecipeRepository", "✅ Receta actualizada con éxito")

                    true
                } else {
                    Log.e("RecipeRepository", "❌ Error en la API: ${response.errorBody()?.string()}")
                    false
                }

            } catch (e: Exception) {
                Log.e("RecipeRepository", "⚠️ Excepción en updateRecipe: ${e.message}")
                triggerNotification("update") // 🔔 Notificación de actualización
                false
            }
        }
    }



    private fun triggerNotification(actionType: String) {
        val workRequest = OneTimeWorkRequestBuilder<RecipeWorker>()
            .setInputData(workDataOf("action_type" to actionType))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("RecipeRepository", "🔔 Notificación programada: Acción -> $actionType")
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
