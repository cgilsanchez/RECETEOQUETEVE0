package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.*
import javax.inject.Inject

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

    /**
     * Obtiene una receta por ID y devuelve su estado actualizado.
     */
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

    /**
     * Actualiza el estado de favorito de una receta en Strapi.
     */
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean) {
        try {
            val requestBody = mapOf(
                "data" to mapOf(
                    "isFavorite" to isFavorite
                )
            )

            val response = api.updateRecipe(recipeId, requestBody)

            if (!response.isSuccessful) {
                Log.e("RecipeRepository", "Error al actualizar favorito: ${response.errorBody()?.string()}")
            }

        } catch (e: Exception) {
            Log.e("RecipeRepository", "Excepción al actualizar favorito: ${e.message}")
        }
    }

    suspend fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int): Boolean {
        return try {
            val requestBody = mapOf(
                "data" to mapOf(
                    "name" to recipeRequest.name,
                    "descriptions" to recipeRequest.descriptions,
                    "ingredients" to recipeRequest.ingredients,
                    "chef" to recipeRequest.chef,
                    "imageUrl" to recipeRequest.imageUrl,
                    "isFavorite" to recipeRequest.isFavorite
                )
            )

            val response = api.updateRecipe(recipeId, requestBody)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error al actualizar receta: ${e.message}")
            false
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
