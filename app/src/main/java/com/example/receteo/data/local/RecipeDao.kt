package com.example.receteo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    suspend fun getFavoriteRecipes(): List<RecipeEntity>

    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity) // Cambiado de RecipeModel a RecipeEntity

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)
}
