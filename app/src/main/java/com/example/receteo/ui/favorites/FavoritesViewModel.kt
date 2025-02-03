package com.example.receteo.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.receteo.data.remote.models.RecipeModel
import javax.inject.Inject

class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _favorites = MutableLiveData<MutableList<RecipeModel>>(mutableListOf())
    val favorites: MutableLiveData<MutableList<RecipeModel>> get() = _favorites

    fun toggleFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value ?: mutableListOf()

        if (currentList.any { it.id == recipe.id }) {
            currentList.removeAll { it.id == recipe.id } // Eliminar si ya está en favoritos
        } else {
            currentList.add(recipe.copy(isFavorite = true)) // Agregar con isFavorite = true
        }

        _favorites.postValue(currentList)
    }


    fun addFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value ?: mutableListOf()
        if (!currentList.any { it.id == recipe.id }) {
            currentList.add(recipe.copy(isFavorite = true))
            _favorites.postValue(currentList)
        }
    }

    fun removeFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value ?: mutableListOf()
        currentList.removeAll { it.id == recipe.id }
        _favorites.postValue(currentList)
    }

}
