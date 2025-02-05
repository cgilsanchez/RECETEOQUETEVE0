package com.example.receteo.ui.chef

import android.util.Log
import androidx.lifecycle.*
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.repository.ChefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChefViewModel @Inject constructor(
    private val repository: ChefRepository
) : ViewModel() {

    private val _chefs = MutableLiveData<List<ChefModel>>()
    val chefs: LiveData<List<ChefModel>> get() = _chefs

    private val _selectedChef = MutableLiveData<ChefModel?>()
    val selectedChef: LiveData<ChefModel?> get() = _selectedChef

    fun fetchChefs() {
        viewModelScope.launch {
            _chefs.value = repository.getChefs()
        }
    }

    fun createChef(name: String) {
        viewModelScope.launch {
            try {
                val success = repository.createChef(name)
                if (success) {
                    fetchChefs()  // Recargar la lista después de crear
                    Log.d("ChefViewModel", "Chef creado correctamente.")
                } else {
                    Log.e("ChefViewModel", "Error al crear el chef.")
                }
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error en createChef: ${e.message}")
            }
        }
    }


    fun updateChef(id: Int, name: String) {
        viewModelScope.launch {
            try {
                val success = repository.updateChef(id, name)
                if (success) {
                    fetchChefs()
                    Log.d("ChefViewModel", "Chef actualizado correctamente.")
                } else {
                    Log.e("ChefViewModel", "Error al actualizar el chef.")
                }
            } catch (e: Exception) {
                Log.e("ChefViewModel", "Error en updateChef: ${e.message}")
            }
        }
    }


    fun deleteChef(id: Int) {
        viewModelScope.launch {
            repository.deleteChef(id)
            fetchChefs() // Recargar lista tras la eliminación
        }
    }
}
