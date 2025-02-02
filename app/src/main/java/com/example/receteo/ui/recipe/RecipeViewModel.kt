package com.example.receteo.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.data.remote.models.RecipeRequestModel
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

    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = repository.getRecipes()
                _recipes.postValue(response)  // 🔥 Asegurar actualización de UI
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes", e)
            }
        }
    }


    fun createRecipe(recipe: RecipeRequestModel) {
        viewModelScope.launch {
            repository.createRecipe(recipe)
            fetchRecipes()  // 🔥 Asegurar actualización
        }
    }

    fun getRecipeById(id: Int) {
        viewModelScope.launch {
            _selectedRecipe.postValue(repository.getRecipeById(id))
        }
    }

    fun updateRecipe(recipe: RecipeRequestModel, id: Int) {
        viewModelScope.launch {
            repository.updateRecipe(recipe, id)
            fetchRecipes()  // 🔥 Asegurar actualización
        }
    }

    fun deleteRecipe(id: Int) {
        viewModelScope.launch {
            repository.deleteRecipe(id)
            fetchRecipes()  // 🔥 Asegurar actualización después de eliminar
        }
    }
}
