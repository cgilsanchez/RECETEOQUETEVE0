package com.example.receteo.data.repository

import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.data.remote.models.RecipeRequestModel
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val api: RecipeApi) {

    suspend fun getRecipes(): List<RecipeModel>? {
        return try {
            val response = api.getRecipes()
            if (response.isSuccessful) {
                response.body()?.data?.map { it.attributes }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createRecipe(recipe: RecipeRequestModel): RecipeModel? {
        val response = api.createRecipe(recipe)
        return if (response.isSuccessful) response.body()?.data?.firstOrNull()?.attributes else null
    }

    suspend fun updateRecipe(recipe: RecipeRequestModel, id: Int): RecipeModel? {
        val response = api.updateRecipe(id, recipe)
        return if (response.isSuccessful) response.body()?.data?.firstOrNull()?.attributes else null
    }

    suspend fun getRecipeById(id: Int): RecipeModel? {
        val response = api.getRecipeById(id)
        return if (response.isSuccessful) response.body()?.data?.firstOrNull()?.attributes else null
    }

    suspend fun deleteRecipe(id: Int): Boolean {
        val response = api.deleteRecipe(id)
        return response.isSuccessful
    }
}
