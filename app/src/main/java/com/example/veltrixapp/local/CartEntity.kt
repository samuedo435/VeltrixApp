package com.example.veltrixapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productoId: Long,
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)