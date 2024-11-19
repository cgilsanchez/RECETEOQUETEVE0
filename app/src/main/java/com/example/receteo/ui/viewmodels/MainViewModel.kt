package com.example.receteo.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeproject.data.db.RecipeEntity
import com.example.recipeproject.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeEntity>>()
    val recipes: LiveData<List<RecipeEntity>> get() = _recipes

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        viewModelScope.launch {
            val recipeList = recipeRepository.getRecipes().first()
            _recipes.postValue(recipeList)
        }
    }

    fun addRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeRepository.addRecipe(recipe)
            fetchRecipes() // Refresh the list after adding
        }
    }

    fun toggleFavorite(recipe: RecipeEntity) {
        viewModelScope.launch {
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            recipeRepository.addRecipe(updatedRecipe)
            fetchRecipes() // Refresh the list
        }
    }
}