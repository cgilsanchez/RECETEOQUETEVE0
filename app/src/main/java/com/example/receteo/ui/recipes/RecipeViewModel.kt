package com.example.receteo.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.model.Recipe
import com.example.receteo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun fetchRecipes() {
        viewModelScope.launch {
            val recipeList = recipeRepository.getAllRecipes()
            _recipes.value = recipeList
        }
    }

    fun getRecipeById(id: String): LiveData<Recipe> {
        val recipe = MutableLiveData<Recipe>()
        viewModelScope.launch {
            recipe.value = recipeRepository.getRecipeById(id)
        }
        return recipe
    }
}