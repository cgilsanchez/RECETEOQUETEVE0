package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.models.ChefDataRequest
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.ChefRequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
        return try {
            val requestBody = ChefRequestModel(ChefDataRequest(name))
            val response = api.createChef(requestBody)

            Log.d("ChefRepository", "📨 Enviando solicitud: $requestBody")
            Log.d("ChefRepository", "🔄 Código de respuesta: ${response.code()} - ${response.message()}")

            if (!response.isSuccessful) {
                Log.e("ChefRepository", "❌ Error en respuesta: ${response.errorBody()?.string()}")
                return false
            }

            delay(500) // Pequeño delay para evitar cancelación prematura
            true

        } catch (e: CancellationException) {
            Log.w("ChefRepository", "⚠️ Corrutina cancelada después de la solicitud.")
            false
        } catch (e: Exception) {
            Log.e("ChefRepository", "🚨 Error al crear chef: ${e.message}")
            false
        }
    }






    suspend fun updateChef(id: Int, name: String): Boolean {
        return try {
            val requestBody = ChefRequestModel(ChefDataRequest(name))
            val response = api.updateChef(id, requestBody)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ChefRepository", "Error al actualizar chef: ${e.message}")
            false
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