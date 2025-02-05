package com.example.receteo.data.remote.models

data class ChefModel(
    val id: Int,
    val name: String
)

data class ChefResponse(
    val data: List<ChefData>
)

data class ChefData(
    val id: Int,
    val attributes: ChefAttributes
)

data class ChefAttributes(
    val name: String
)
