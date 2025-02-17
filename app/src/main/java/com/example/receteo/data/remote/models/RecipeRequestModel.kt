package com.example.receteo.data.remote.models

data class RecipeRequestModel(
    val data: RecipeDataRequest
)

data class RecipeDataRequest(
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val chef: Int,
    val image: List<Int>?, // ✅ Ahora Strapi recibirá correctamente el ID de la imagen
    val isFavorite: Boolean
)
