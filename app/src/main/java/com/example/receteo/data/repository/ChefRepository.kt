package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.models.ChefDataRequest
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.ChefRequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChefRepository @Inject constructor(private val api: ChefApi) {

    suspend fun getChefs(): List<ChefModel> {
        return try {
            val response = api.getChefs()
            if (response.isSuccessful) {
                response.body()?.data?.map { chefData ->
                    ChefModel(
                        id = chefData.id,
                        name = chefData.attributes.name
                    )
                } ?: emptyList()
            } else {
                Log.e("ChefRepository", "Error en la API: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ChefRepository", "Excepción obteniendo chefs: ${e.message}")
            emptyList()
        }
    }

    suspend fun getChefById(id: Int): ChefModel? {
        return try {
            val response = api.getChefById(id)
            if (response.isSuccessful) {
                response.body()?.data?.firstOrNull()?.let { chefData ->
                    ChefModel(
                        id = chefData.id,
                        name = chefData.attributes.name
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ChefRepository", "Error al obtener chef: ${e.message}")
            null
        }
    }

    suspend fun createChef(name: String): Boolean {
        return withContext(Dispatchers.IO) {  // Evita cancelaciones prematuras
            try {
                val requestBody = ChefRequestModel(ChefDataRequest(name))
                val response = api.createChef(requestBody)
                Log.d("ChefRepository", "Respuesta al crear chef: ${response.code()} - ${response.errorBody()?.string()}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ChefRepository", "Error al crear chef: ${e.message}")
                false
            }
        }
    }


    suspend fun updateChef(id: Int, name: String): Boolean {
        return withContext(Dispatchers.IO) {  // Forzar a ejecutarse en un hilo secundario
            try {
                val requestBody = ChefRequestModel(ChefDataRequest(name))
                val response = api.updateChef(id, requestBody)
                Log.d("ChefRepository", "Respuesta al actualizar chef: ${response.code()} - ${response.errorBody()?.string()}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("ChefRepository", "Error al actualizar chef: ${e.message}")
                false
            }
        }
    }




    suspend fun deleteChef(id: Int): Boolean {
        return try {
            val response = api.deleteChef(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ChefRepository", "Error al eliminar chef: ${e.message}")
            false
        }
    }
}