package com.example.receteo.data.repository

import com.example.receteo.data.local.RecipeDao
import com.example.receteo.data.local.RecipeEntity
import com.example.receteo.data.remote.RecipeApi
import javax.inject.Inject

class
RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeApi: RecipeApi
) {
    suspend fun getAllRecipes() = recipeDao.getAllRecipes()
    suspend fun getFavoriteRecipes() = recipeDao.getFavoriteRecipes()
    suspend fun createRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe)
        recipeApi.createRecipe(recipe)
    }
    suspend fun updateRecipe(recipe: RecipeEntity) {
        recipeDao.updateRecipe(recipe)
        recipeApi.updateRecipe(recipe.id, recipe)
    }
    suspend fun deleteRecipe(id: Int) {
        recipeDao.deleteRecipeById(id)
        recipeApi.deleteRecipe(id)
    }
}