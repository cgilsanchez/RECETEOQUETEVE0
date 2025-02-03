package com.example.receteo.ui.recipe

import android.util.Log
import androidx.lifecycle.*
import com.example.receteo.data.remote.models.*
import com.example.receteo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeModel>>()
    val recipes: LiveData<List<RecipeModel>> get() = _recipes

    private val _selectedRecipe = MutableLiveData<RecipeModel?>()
    val selectedRecipe: LiveData<RecipeModel?> get() = _selectedRecipe

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

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
                    _errorMessage.postValue("Receta no encontrada.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al obtener la receta: ${e.message}")
            }
        }
    }



    fun createRecipe(recipeRequest: RecipeRequestModel) {
        viewModelScope.launch {
            try {
                repository.createRecipe(recipeRequest)
                fetchRecipes()
            } catch (e: Exception) {
                _errorMessage.postValue("Error al crear receta: ${e.message}")
            }
        }
    }

    fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipeRequest, recipeId)
                fetchRecipes()
            } catch (e: Exception) {
                _errorMessage.postValue("Error al actualizar receta: ${e.message}")
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
}
