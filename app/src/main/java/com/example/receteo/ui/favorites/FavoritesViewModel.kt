package com.example.receteo.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.RecipeModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _favorites = MutableLiveData<MutableList<RecipeModel>>(mutableListOf())
    val favorites: MutableLiveData<MutableList<RecipeModel>> get() = _favorites

    fun toggleFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value ?: mutableListOf()

        if (currentList.any { it.id == recipe.id }) {
            currentList.removeAll { it.id == recipe.id } // Si ya está, lo quitamos de favoritos
        } else {
            currentList.add(recipe.copy(isFavorite = true)) // Si no está, lo agregamos
        }

        _favorites.postValue(currentList.toMutableList()) // Actualizamos LiveData
    }


    fun getFavorites(): List<RecipeModel> {
        return _favorites.value ?: emptyList()
    }

    fun loadFavorites(allRecipes: List<RecipeModel>) {
        _favorites.postValue(allRecipes.filter { it.isFavorite }.toMutableList())
    }

}





