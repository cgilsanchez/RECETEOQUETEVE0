package com.example.receteo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val image: String?,
    val isFavorite: Boolean = false
)
