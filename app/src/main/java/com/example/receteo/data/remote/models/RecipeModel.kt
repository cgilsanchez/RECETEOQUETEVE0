package com.example.receteo.data.remote.models

data class RecipeModel(
    val id: Int,
    val name: String?,
    val descriptions: String?,
    val ingredients: String?,
    val chef: Int?,
    val imageUrl: String?,
    val isFavorite: Boolean
)


data class RecipeResponse(
    val data: List<RecipeData> // ✅ Debería ser una lista de `RecipeData`
)


data class RecipeRespons( // Para obtener UNA receta por ID (Objeto único)
    val data: RecipeData
)


data class RecipeData(
    val id: Int,
    val attributes: RecipeAttributes
)

data class RecipeAttributes(
    val name: String,
    val descriptions: String,
    val ingredients: String,
    val chef: ChefDataa?, // 🔥 Corregido: `Int?` en lugar de `String?`
    val image: ImageData?,
    val isFavorite: Boolean = false
)

data class ImageData(
    val data: ImageAttributes? // 🔥 Corregido: `data` puede ser null
)

data class ImageAttributes(
    val attributes: ImageFormats?
)

data class ImageFormats(
    val url: String?
) {
    fun getImageUrl(): String {
        return url ?: "" // 🔥 Devuelve un string vacío si la URL es null
    }
}


data class ChefDataa(
    val data: ChefInfo? // ✅ Aquí `data` es un objeto con `id`
)

data class ChefInfo(
    val id: Int // ✅ Aquí está el `id` correcto del chef
)


