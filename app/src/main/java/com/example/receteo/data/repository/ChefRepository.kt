package com.example.receteo.data.repository

import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.models.ChefModel
import javax.inject.Inject

class ChefRepository @Inject constructor(private val chefApi: ChefApi) {

    suspend fun getAllChefs() = chefApi.getAllChefs()

    suspend fun createChef(chef: ChefModel) = chefApi.createChef(chef)

    suspend fun updateChef(id: Int, chef: ChefModel) = chefApi.updateChef(id, chef)

    suspend fun deleteChef(id: Int) = chefApi.deleteChef(id)
}