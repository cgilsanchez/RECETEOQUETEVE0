package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val api: RecipeApi) {

    /**
     * Obtiene todas las recetas de la API y las convierte en RecipeModel.
     */
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
                        createdAt = recipeData.attributes.createdAt,
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: "",
                        isFavorite = false // 🔥 Se asigna un valor por defecto
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
            val response = api.getRecipeById(recipeId) // Llamada a la API
            if (response.isSuccessful) {
                val recipeData = response.body()?.data?.firstOrNull() // 🔥 Acceder al primer elemento si es una lista
                recipeData?.let {
                    RecipeModel(
                        id = it.id,
                        name = it.attributes.name,
                        descriptions = it.attributes.descriptions,
                        ingredients = it.attributes.ingredients,
                        createdAt = it.attributes.createdAt,
                        imageUrl = it.attributes.image?.data?.attributes?.url ?: "",
                        isFavorite = false // 🔥 Se asigna un valor por defecto
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





    /**
     * Crea una nueva receta.
     */
    suspend fun createRecipe(recipeRequest: RecipeRequestModel): Boolean {
        return try {
            val response = api.createRecipe(recipeRequest)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Actualiza una receta existente por ID.
     */
    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int): Boolean {
        return try {
            val response = api.updateRecipe(recipeId, recipeRequest)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Elimina una receta por ID.
     */
    suspend fun deleteRecipe(recipeId: Int): Boolean {
        return try {
            Log.d("RecipeRepository", "Realizando solicitud DELETE para receta ID: $recipeId")
            val response = api.deleteRecipe(recipeId)
            if (response.isSuccessful) {
                Log.d("RecipeRepository", "Receta eliminada en Strapi")
                true
            } else {
                Log.e("RecipeRepository", "Error en DELETE: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Excepción eliminando receta: ${e.message}")
            false
        }
    }

}
