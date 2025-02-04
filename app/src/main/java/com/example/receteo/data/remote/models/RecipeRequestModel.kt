package com.example.receteo.data.remote.models

data class RecipeRequestModel(
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val chef: Int,
    val imageUrl: String,
    val isFavorite: Boolean // ✅ Se asegura de incluir el estado de favorito en las actualizaciones
)
