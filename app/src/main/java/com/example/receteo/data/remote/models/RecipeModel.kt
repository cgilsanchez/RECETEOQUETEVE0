package com.example.receteo.data.remote.models

data class RecipeModel(
    val id: Int,
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val createdAt: String,
    val imageUrl: String,
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
    val createdAt: String,
    val image: ImageData?
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
        return url ?: ""  // Devuelve la URL de la imagen o un string vacío si no hay imagen
    }
}
