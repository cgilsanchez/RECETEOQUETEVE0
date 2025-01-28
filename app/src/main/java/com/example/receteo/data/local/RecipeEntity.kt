package com.example.receteo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val image: String?, // URL de la imagen
    val isFavorite: Boolean = false
)
