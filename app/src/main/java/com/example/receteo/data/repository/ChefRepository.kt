package com.example.receteo.data.repository

import android.util.Log
import com.example.receteo.data.local.ChefDao
import com.example.receteo.data.local.ChefEntity
import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.models.ChefDataRequest
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.ChefRequestModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ChefRepository @Inject constructor(
    private val api: ChefApi,
    private val chefDao: ChefDao
) {

    suspend fun getChefs(): List<ChefModel> {
        return try {
            val response = api.getChefs()
            val chefs = if (response.isSuccessful) {
                response.body()?.data?.map { chefData ->
                    ChefModel(
                        id = chefData.id,
                        name = chefData.attributes.name
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }


            chefDao.insertAll(chefs.map {
                ChefEntity(id = it.id, name = it.name)
            })

            chefs
        } catch (e: Exception) {
            Log.e("ChefRepository", "Error de red, usando Room: ${e.message}")

            chefDao.getAll().first().map {
                ChefModel(id = it.id, name = it.name)
            }
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

            if (!response.isSuccessful) {
                Log.e("ChefRepository", "Error en respuesta: ${response.errorBody()?.string()}")
                return false
            }

            delay(500)
            true

        } catch (e: CancellationException) {
            false
        } catch (e: Exception) {
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