package com.example.receteo.ui.chef

import android.os.Build
import androidx.annotation.RequiresApi
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

    private val _chefs = mutableListOf<ChefModel>()
    val chefs: List<ChefModel> get() = _chefs

    fun fetchChefs() {
        viewModelScope.launch {
            try {
                val response = repository.getAllChefs()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _chefs.clear()
                        _chefs.addAll(it)
                    }
                } else {
                    println("Error al obtener chefs: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción en fetchChefs: ${e.message}")
            }
        }
    }

    fun createChef(chef: ChefModel) {
        viewModelScope.launch {
            try {
                val response = repository.createChef(chef)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _chefs.add(it)
                    }
                } else {
                    println("Error al crear chef: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción en createChef: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteChef(chefId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteChef(chefId)
                if (response.isSuccessful) {
                    _chefs.removeIf { it.id == chefId }
                } else {
                    println("Error al eliminar chef: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Excepción en deleteChef: ${e.message}")
            }
        }
    }
}