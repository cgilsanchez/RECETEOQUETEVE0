package com.example.receteo.data.remote.models

data class RecipeModel(
    val id: Int,
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val image: String?, // URL de la imagen
    val chef: ChefModel?
)

data class RecipeResponse(
    val data: List<RecipeModel>
)