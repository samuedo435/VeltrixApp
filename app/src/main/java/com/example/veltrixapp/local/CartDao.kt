package com.example.veltrixapp.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun obtenerCarrito(): List<CartEntity>

    // Si el producto ya está en el carrito, lo reemplaza (útil para actualizar cantidad)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(item: CartEntity)

    @Delete
    suspend fun eliminarProducto(item: CartEntity)

    @Query("DELETE FROM cart_items")
    suspend fun vaciarCarrito()
}