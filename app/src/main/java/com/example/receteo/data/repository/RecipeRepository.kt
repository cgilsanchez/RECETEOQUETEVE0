package com.example.receteo.data.repository

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.receteo.data.local.RecipeDao
import com.example.receteo.data.local.RecipeEntity
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import com.example.receteo.ui.notification.RecipeWorker
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
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


class RecipeRepository @Inject constructor(private val api: RecipeApi,
                                           private val recipeDao: RecipeDao, private val context: Context) {



    suspend fun getRecipes(): List<RecipeModel> {
        return try {
            val response = api.getRecipes()
            val recipes = if (response.isSuccessful) {
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
                emptyList()
            }

            // Guardar en Room
            recipeDao.insertAll(
                recipes.map {
                    RecipeEntity(
                        id = it.id,
                        name = it.name ?: "",
                        descriptions = it.descriptions ?: "",
                        ingredients = it.ingredients ?: "",
                        image = it.imageUrl ?: "",
                        isFavorite = it.isFavorite
                    )
                }
            )

            recipes
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Fallo de red, usando Room: ${e.message}")


            recipeDao.getAll().first().map {
                RecipeModel(
                    id = it.id,
                    name = it.name,
                    descriptions = it.descriptions,
                    ingredients = it.ingredients,
                    chef = null,
                    imageUrl = it.image,
                    isFavorite = it.isFavorite
                )
            }
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
                        chef = recipeData.attributes.chef?.data?.id ?: 0,
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "",
                        isFavorite = recipeData.attributes.isFavorite ?: false
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Excepción en getRecipeById: ${e.message}")
            null
        }
    }





    suspend fun createRecipe(recipeRequest: RecipeRequestModel, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO + SupervisorJob()) {
            try {

                val imageId = imageFile?.let { uploadImage(it) }
                if (imageId == null && imageFile != null) {
                    return@withContext false
                }

                val imageList = mutableListOf<Map<String, Int>>()
                imageId?.let { imageList.add(mapOf("id" to it)) }

                val updatedRequest = mapOf(
                    "data" to mapOf(
                        "chef" to recipeRequest.data.chef,
                        "descriptions" to recipeRequest.data.descriptions,
                        "image" to imageList,
                        "ingredients" to recipeRequest.data.ingredients,
                        "isFavorite" to recipeRequest.data.isFavorite,
                        "name" to recipeRequest.data.name
                    )
                )

                val jsonBody = Gson().toJson(updatedRequest).toRequestBody("application/json".toMediaTypeOrNull())

                val response = api.createRecipe(jsonBody)

                return@withContext if (response.isSuccessful) {

                    true
                } else {
                    false
                }

            } catch (e: Exception) {
                Log.e("RecipeRepository", "Excepción en createRecipe: ${e.message}")
                triggerNotification("create")
                true
            }
        }
    }


    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                val existingRecipe = getRecipeById(recipeId)

                val imageList = if (imageFile != null) {
                    val newImageId = uploadImage(imageFile)
                    if (newImageId != null) {
                        listOf(mapOf("id" to newImageId))
                    } else {
                        emptyList()
                    }
                } else if (existingRecipe?.imageUrl?.isNotBlank() == true) {
                    emptyList()
                } else {
                    emptyList()
                }

                val updatedRequest = mapOf(
                    "data" to mapOf(
                        "chef" to recipeRequest.data.chef,
                        "descriptions" to recipeRequest.data.descriptions,
                        "ingredients" to recipeRequest.data.ingredients,
                        "isFavorite" to recipeRequest.data.isFavorite,
                        "name" to recipeRequest.data.name

                    ).let { map -> if (imageFile != null) {
                            map + ("image" to imageList)
                            } else {
                            map
                        }
                    }
                )

                val jsonBody = Gson().toJson(updatedRequest).toRequestBody("application/json".toMediaTypeOrNull())

                val response = api.updateRecipe(recipeId, jsonBody)
                return@withContext if (response.isSuccessful) {
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", " Excepción en updateRecipe: ${e.message}")
                triggerNotification("update")
                true
            }
        }
    }



    private fun triggerNotification(actionType: String) {
        val workRequest = OneTimeWorkRequestBuilder<RecipeWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("action_type" to actionType))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("RecipeRepository", "Notificación programada: Acción -> $actionType")
    }






    suspend fun uploadImage(imageFile: File): Int? {
        return withContext(Dispatchers.IO + SupervisorJob()) {
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("files", imageFile.name, requestFile)


                val response = api.uploadImage(body)

                if (response.isSuccessful) {
                    val uploadedId = response.body()?.firstOrNull()?.id
                    uploadedId
                } else {
                    null
                }
            } catch (e: SocketTimeoutException) {
                Log.e("RecipeRepository", "Timeout en uploadImage: Reintentando...")
                delay(2000)
                uploadImage(imageFile)
            } catch (e: IOException) {
                null
            } catch (e: Exception) {
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
