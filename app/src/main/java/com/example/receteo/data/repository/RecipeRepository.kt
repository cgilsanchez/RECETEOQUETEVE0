package com.example.receteo.data.repository

import com.example.recipeproject.data.api.RecipeApi
import com.example.recipeproject.data.db.RecipeDao
import com.example.recipeproject.data.db.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val recipeApi: RecipeApi
) {
    fun getRecipes(): Flow<List<RecipeEntity>> = flow {
        val localData = recipeDao.getAllRecipes().first()
        emit(localData)
    }

    suspend fun addRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe)
    }
}