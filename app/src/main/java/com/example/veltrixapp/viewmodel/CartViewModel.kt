package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.local.CartEntity
import com.example.veltrixapp.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartEntity>>(emptyList())
    val cartItems: StateFlow<List<CartEntity>> = _cartItems.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItems()
        }
    }

    fun addProduct(id: Long, nombre: String, precio: Double) {
        viewModelScope.launch {
            // Se verifica si ya está en el carrito para sumarle 1 a la cantidad
            val currentList = _cartItems.value
            val existingItem = currentList.find { it.productoId == id }

            val newItem = if (existingItem != null) {
                existingItem.copy(cantidad = existingItem.cantidad + 1)
            } else {
                CartEntity(productoId = id, nombre = nombre, precio = precio, cantidad = 1)
            }

            repository.addToCart(newItem)
            loadCart() // Se recarga la lista actualizada
        }
    }

    fun removeProduct(item: CartEntity) {
        viewModelScope.launch {
            repository.removeFromCart(item)
            loadCart()
        }
    }
}

class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}