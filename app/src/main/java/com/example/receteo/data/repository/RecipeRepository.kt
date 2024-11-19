package com.example.receteo.data.repository

import com.example.receteo.data.Api.RecipeApi
import com.example.receteo.data.db.RecipeDao
import com.example.receteo.data.db.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val recipeApi: RecipeApi
) {

    // Obtener todas las recetas (desde API y Room)
    fun getRecipes(): Flow<List<RecipeEntity>> = flow {
        val remoteData = recipeApi.getRecipes() // Llama a la API
        remoteData.forEach { recipeDao.insertRecipe(it) } // Guarda en Room
        val localData = recipeDao.getAllRecipes().first() // Obtén datos de Room
        emit(localData)
    }

    // Agregar receta (en Room y en la API)
    suspend fun addRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe) // Guarda en Room
        recipeApi.createRecipe(recipe) // Envía a la API
    }

    // Eliminar receta
    suspend fun deleteRecipe(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe) // Elimina en Room
        // Nota: Agrega un endpoint en Strapi si quieres eliminar remotamente
    }
}
