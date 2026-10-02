package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.veltrixapp.model.Shoe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CatalogViewModel : ViewModel() {

    // Estado interno inmutable para la vista, mutable internamente
    private val _shoes = MutableStateFlow<List<Shoe>>(emptyList())
    val shoes: StateFlow<List<Shoe>> = _shoes.asStateFlow()

    private val _cart = MutableStateFlow<List<Shoe>>(emptyList())
    val cart: StateFlow<List<Shoe>> = _cart.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Simula la carga de datos del backend.
     * Aquí integraremos Retrofit para consumir la API de Spring Boot posteriormente.
     */
    private fun loadMockData() {
        _shoes.value = listOf(
            Shoe(1, "Nike Air Max", "Hombre", 120.0),
            Shoe(2, "Adidas Ultraboost", "Mujer", 150.0),
            Shoe(3, "Puma RS-X", "Unisex", 90.0),
            Shoe(4, "Vans Old Skool", "Unisex", 75.0)
        )
    }

    /**
     * Agrega un zapato al estado global del carrito.
     */
    fun addToCart(shoe: Shoe) {
        val currentCart = _cart.value.toMutableList()
        currentCart.add(shoe)
        _cart.value = currentCart
    }
}