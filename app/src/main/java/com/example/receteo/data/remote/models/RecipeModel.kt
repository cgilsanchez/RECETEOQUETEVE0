package com.example.receteo.data.remote.models

data class RecipeResponse(
    val data: List<RecipeData>
)

data class RecipeData(
    val id: Int,
    val attributes: RecipeModel
)

data class RecipeModel(
    val id: Int,
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val createdAt: String,
    val imageUrl: String
)


