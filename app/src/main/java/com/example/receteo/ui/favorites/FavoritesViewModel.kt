package com.example.receteo.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.receteo.data.remote.models.RecipeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _favorites = MutableLiveData<List<RecipeModel>>()
    val favorites: LiveData<List<RecipeModel>> get() = _favorites

    fun addFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value?.toMutableList() ?: mutableListOf()
        if (!currentList.contains(recipe)) {
            currentList.add(recipe)
            _favorites.postValue(currentList)
        }
    }

    fun removeFavorite(recipe: RecipeModel) {
        val currentList = _favorites.value?.toMutableList() ?: mutableListOf()
        currentList.remove(recipe)
        _favorites.postValue(currentList)
    }
}