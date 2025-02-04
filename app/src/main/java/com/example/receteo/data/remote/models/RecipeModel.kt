package com.example.receteo.data.remote.models

data class RecipeModel(
    val id: Int,
    val name: String,
    val ingredients: String,
    val descriptions: String,
    val chef: String?,
    val imageUrl: String?,
    val isFavorite: Boolean = false // ✅ Se asegura de incluir el estado de favorito
)

data class RecipeResponse(
    val data: List<RecipeData>
)

data class RecipeData(
    val id: Int,
    val attributes: RecipeAttributes
)

data class RecipeAttributes(
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val chef: String?,
    val image: ImageData?,
    val isFavorite: Boolean = false
)

data class ImageData(
    val data: ImageAttributes?
)

data class ImageAttributes(
    val attributes: ImageFormats?
)

data class ImageFormats(
    val url: String?
) {
    fun getImageUrl(): String {
        return url ?: ""
    }
}
