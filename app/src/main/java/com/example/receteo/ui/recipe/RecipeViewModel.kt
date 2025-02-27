package com.example.receteo.ui.recipe

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager

import com.example.receteo.data.remote.models.*
import com.example.receteo.data.repository.RecipeRepository
import com.example.receteo.ui.favorites.FavoritesViewModel
import com.example.receteo.ui.notification.RecipeWorker
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val favoritesViewModel: FavoritesViewModel,
    application: Application
) : AndroidViewModel(application) {

    private val _recipes = MutableLiveData<List<RecipeModel>>()
    val recipes: LiveData<List<RecipeModel>> get() = _recipes

    private val _selectedRecipe = MutableLiveData<RecipeModel?>()
    val selectedRecipe: LiveData<RecipeModel?> get() = _selectedRecipe

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _creationState = MutableLiveData<Boolean>()
    val creationState: LiveData<Boolean> get() = _creationState

    // Se añade successMessage para corregir el error de referencia no resuelta
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> get() = _successMessage

    fun fetchRecipes() {
        viewModelScope.launch {
            try {
                val response = repository.getRecipes()
                if (!response.isNullOrEmpty()) {
                    _recipes.postValue(response)
                } else {
                    _errorMessage.postValue("No se encontraron recetas.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al obtener recetas: ${e.message}")
            }
        }
    }

    fun getRecipeById(recipeId: Int) {
        viewModelScope.launch {
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    // 🔥 Asegurar que la imagen siempre tenga un valor correcto
                    val fixedRecipe = recipe.copy(imageUrl = recipe.imageUrl ?: "")

                    _selectedRecipe.postValue(fixedRecipe)
                    Log.d("RecipeViewModel", "📥 Receta obtenida con imagen: ${fixedRecipe.imageUrl}")
                } else {
                    _errorMessage.postValue("❌ Receta no encontrada en Strapi")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("🚨 Error al obtener la receta: ${e.message}")
            }
        }
    }



    fun createRecipe(recipeRequest: RecipeRequestModel, imageFile: File?) {
        viewModelScope.launch {
            try {
                Log.d("RecipeViewModel", "📤 Creando receta...")
                val success = repository.createRecipe(recipeRequest, imageFile)

                if (success) {
                    _successMessage.postValue("✅ Receta creada con éxito")
                    fetchRecipes()
                    scheduleNotificationWorker() // 🔥 Ejecutar Worker después de crear una receta
                } else {
                    _errorMessage.postValue("❌ Error al crear receta")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("❌ Error en la creación de receta: ${e.message}")
            }
        }
    }

    fun updateRecipe(recipeRequest: RecipeRequestModel, recipeId: Int, imageFile: File?) {
        viewModelScope.launch {
            try {
                val success = repository.updateRecipe(recipeRequest, recipeId, imageFile)
                if (success) {
                    _successMessage.postValue("✅ Receta actualizada con éxito")
                    fetchRecipes()
                    getRecipeById(recipeId)
                    scheduleNotificationWorker() // 🔥 Ejecutar Worker después de actualizar una receta
                } else {
                    _errorMessage.postValue("Error al actualizar la receta.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al actualizar la receta: ${e.message}")
            }
        }
    }

    fun scheduleNotificationWorker() {
        val context = getApplication<Application>().applicationContext
        val workRequest = OneTimeWorkRequestBuilder<RecipeWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST) // 🔥 Se ejecuta inmediatamente
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("RecipeWorker", "✅ Worker programado correctamente desde ViewModel")
    }


    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.deleteRecipe(recipeId)
                if (success) {
                    _recipes.value = _recipes.value?.filter { it.id != recipeId }?.toMutableList()
                } else {
                    _errorMessage.postValue("Error eliminando la receta.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error eliminando la receta: ${e.message}")
            }
        }
    }

    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val updatedRecipes = recipes.value?.map { recipe ->
                if (recipe.id == recipeId) {
                    recipe.copy(isFavorite = !(recipe.isFavorite ?: false))

                } else {
                    recipe
                }
            } ?: emptyList() // 🔥 Si es null, usamos una lista vacía
            _recipes.postValue(updatedRecipes)
        }
    }
}
