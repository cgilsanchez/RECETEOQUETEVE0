package com.example.receteo.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChefDao {
    @Query("SELECT * FROM chefs")
    fun getAll(): Flow<List<ChefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chefs: List<ChefEntity>)
}
