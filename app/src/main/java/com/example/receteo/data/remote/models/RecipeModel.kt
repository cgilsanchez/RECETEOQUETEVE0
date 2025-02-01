package com.example.receteo.data.remote.models

data class RecipeResponse(
    val data: List<RecipeModel>
)

data class RecipeModel(
    val id: Int,
    val attributes: RecipeAttributes
)

data class RecipeAttributes(
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val createdAt: String,
    val imageUrl: String
)
