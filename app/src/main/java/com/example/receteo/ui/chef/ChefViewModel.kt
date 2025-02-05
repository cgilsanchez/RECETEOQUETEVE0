package com.example.receteo.ui.chef

import android.util.Log
import androidx.lifecycle.*
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.repository.ChefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ChefViewModel", "📨 Intentando crear chef: $name")

                val success = repository.createChef(name)

                withContext(Dispatchers.Main) {
                    if (success) {
                        fetchChefs() // Refresca la lista en la UI
                        Log.d("ChefViewModel", "✅ Chef creado correctamente.")
                    } else {
                        Log.e("ChefViewModel", "❌ Error al crear el chef.")
                    }
                }

            } catch (e: CancellationException) {
                Log.w("ChefViewModel", "⚠️ Corrutina cancelada después de crear el chef.")
            } catch (e: Exception) {
                Log.e("ChefViewModel", "🚨 Excepción en createChef: ${e.message}")
            }
        }
    }







    fun updateChef(id: Int, name: String) {
        viewModelScope.launch {
            val success = repository.updateChef(id, name)
            if (success) {
                fetchChefs()  // Recargar lista de chefs después de actualizar
                Log.d("ChefViewModel", "Chef actualizado correctamente.")
            } else {
                Log.e("ChefViewModel", "Error al actualizar el chef.")
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
