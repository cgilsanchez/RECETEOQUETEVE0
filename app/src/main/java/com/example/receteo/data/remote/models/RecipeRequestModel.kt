package com.example.receteo.data.remote.models


data class RecipeRequestModel(
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val imageUrl: String // Asegúrate de que este campo existe
)
