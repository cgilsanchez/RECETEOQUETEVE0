package com.example.receteo.ui.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _favoriteRecipes = MutableLiveData<MutableList<String>>(mutableListOf())
    val favoriteRecipes: LiveData<MutableList<String>> get() = _favoriteRecipes

    fun addFavorite(recipe: String) {
        if (!_favoriteRecipes.value!!.contains(recipe)) {
            _favoriteRecipes.value?.add(recipe)
            _favoriteRecipes.postValue(_favoriteRecipes.value) // Notifica cambios
        }
    }

    fun removeFavorite(recipe: String) {
        if (_favoriteRecipes.value!!.contains(recipe)) {
            _favoriteRecipes.value?.remove(recipe)
            _favoriteRecipes.postValue(_favoriteRecipes.value) // Notifica cambios
        }
    }
}
