package com.example.receteo.ui.chef

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.repository.ChefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChefViewModel @Inject constructor(
    private val repository: ChefRepository
) : ViewModel() {

    private val _chefs = MutableLiveData<List<ChefModel>>() // Ahora es LiveData
    val chefs: LiveData<List<ChefModel>> get() = _chefs

    fun fetchChefs() {
        viewModelScope.launch {
            try {
                val response = repository.getAllChefs()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _chefs.postValue(it) // Actualiza LiveData
                    }
                } else {
                    println("Error al obtener chefs: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción en fetchChefs: ${e.message}")
            }
        }
    }
}
