package com.example.veltrixapp.repository

import com.example.veltrixapp.local.CartDao
import com.example.veltrixapp.local.CartEntity

class CartRepository(private val cartDao: CartDao) {

    suspend fun getCartItems(): List<CartEntity> {
        return cartDao.obtenerCarrito()
    }

    suspend fun addToCart(producto: CartEntity) {
        // Al usar REPLACE en el DAO, si el ID ya existe, actualizará los datos (útil para sumar cantidades)
        cartDao.insertarProducto(producto)
    }

    suspend fun removeFromCart(producto: CartEntity) {
        cartDao.eliminarProducto(producto)
    }

    suspend fun clearCart() {
        cartDao.vaciarCarrito()
    }

    // Más adelante, aquí se agrergará la función que llama a RetrofitClient.apiService.checkout()
    // y luego llama a clearCart() si la compra es exitosa.
}