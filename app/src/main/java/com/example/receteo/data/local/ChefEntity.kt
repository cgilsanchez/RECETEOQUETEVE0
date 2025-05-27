package com.example.receteo.data.local



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chefs")
data class ChefEntity(
    @PrimaryKey val id: Int,
    val name: String
)
