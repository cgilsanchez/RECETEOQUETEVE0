package com.example.receteo.data.remote.models

data class ChefModel(
    val id: Int,
    val name: String,
    val recetas: List<RecipeModel>?
)