package com.example.receteo.data.repository

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
                        imageUrl = recipeData.attributes.image?.data?.attributes?.url ?: ""
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
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
            val response = api.deleteRecipe(recipeId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
