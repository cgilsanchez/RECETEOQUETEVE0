package com.example.receteo.data.repository

import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.remote.models.RecipeModel
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeApi: RecipeApi
) {

    suspend fun getAllRecipesFromApi() = recipeApi.getAllRecipes()

    suspend fun createRecipe(recipe: RecipeModel) = recipeApi.createRecipe(recipe)

    suspend fun updateRecipe(id: Int, recipe: RecipeModel) = recipeApi.updateRecipe(id, recipe)

    suspend fun deleteRecipe(id: Int) = recipeApi.deleteRecipe(id)
}
