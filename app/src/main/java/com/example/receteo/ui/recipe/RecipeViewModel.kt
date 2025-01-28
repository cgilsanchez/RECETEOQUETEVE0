package com.example.receteo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.RecipeModel
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

    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = repository.getAllRecipesFromApi()
                if (response.isSuccessful) {
                    _recipes.postValue(response.body())
                } else {
                    println("❌ Error al obtener recetas: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción en fetchRecipes: ${e.message}")
            }
        }
    }

    fun createRecipe(recipe: RecipeModel) {
        viewModelScope.launch {
            try {
                val response = repository.createRecipe(recipe)
                if (response.isSuccessful) {
                    fetchRecipes()
                } else {
                    println("❌ Error al crear receta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción en createRecipe: ${e.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    fetchRecipes()
                } else {
                    println("❌ Error al eliminar receta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción en deleteRecipe: ${e.message}")
            }
        }
    }
}
