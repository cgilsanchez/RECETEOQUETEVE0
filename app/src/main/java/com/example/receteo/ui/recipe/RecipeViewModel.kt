package com.example.receteo.ui.recipe

import android.util.Log
import androidx.lifecycle.*
import com.example.receteo.data.remote.models.*
import com.example.receteo.data.repository.RecipeRepository
import com.example.receteo.ui.favorites.FavoritesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val favoritesViewModel: FavoritesViewModel
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeModel>>()
    val recipes: LiveData<List<RecipeModel>> get() = _recipes

    private val _selectedRecipe = MutableLiveData<RecipeModel?>()
    val selectedRecipe: LiveData<RecipeModel?> get() = _selectedRecipe

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _creationState = MutableLiveData<Boolean>()
    val creationState: LiveData<Boolean> get() = _creationState


    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = repository.getRecipes()
                if (!response.isNullOrEmpty()) {
                    _recipes.postValue(response)
                } else {
                    _errorMessage.postValue("No se encontraron recetas.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al obtener recetas: ${e.message}")
            }
        }
    }

    fun getRecipeById(recipeId: Int) {
        viewModelScope.launch {
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    _selectedRecipe.postValue(recipe)
                } else {
                    _errorMessage.postValue("❌ Receta no encontrada en Strapi")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("🚨 Error al obtener la receta: ${e.message}")
            }
        }
    }


    fun createRecipe(recipeRequest: RecipeRequestModel, imageFile: File?) {
        viewModelScope.launch(SupervisorJob()) {
            try {
                Log.d("RecipeViewModel", "📤 Creando receta...")

                val success = repository.createRecipe(recipeRequest, imageFile)

                if (success) {
                    Log.d("RecipeViewModel", "✅ Receta creada con éxito")
                } else {
                    _errorMessage.postValue("❌ Error al crear receta")
                }
            } catch (e: CancellationException) {
                Log.e("RecipeViewModel", "❌ Corrutina cancelada en ViewModel: ${e.message}")
                _errorMessage.postValue("❌ Operación cancelada")
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "❌ Excepción en ViewModel: ${e.message}")
                _errorMessage.postValue("❌ Error en la creación de receta")
            }
        }
    }





    fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?) {
        viewModelScope.launch {
            try {
                val success = repository.updateRecipe(recipeRequest, recipeId, imageFile)
                if (success) {
                    fetchRecipes() // 🔄 Refresca la lista de recetas después de la actualización
                } else {
                    _errorMessage.postValue("Error al actualizar la receta.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al actualizar la receta: ${e.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.deleteRecipe(recipeId)
                if (success) {
                    _recipes.value = _recipes.value?.filter { it.id != recipeId }?.toMutableList()
                } else {
                    _errorMessage.postValue("Error eliminando la receta.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error eliminando la receta: ${e.message}")
            }
        }
    }


    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val updatedRecipes = recipes.value?.map { recipe ->
                if (recipe.id == recipeId) {
                    recipe.copy(isFavorite = !(recipe.isFavorite ?: false))

                } else {
                    recipe
                }
            } ?: emptyList() // 🔥 Si es null, usamos una lista vacía
            _recipes.postValue(updatedRecipes)
        }
    }


}
