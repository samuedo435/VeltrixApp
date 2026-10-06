package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.data.CheckoutRequest
import com.example.veltrixapp.data.ItemCheckoutDTO
import com.example.veltrixapp.data.RetrofitClient
import com.example.veltrixapp.local.CartEntity
import com.example.veltrixapp.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _metodoPago = MutableStateFlow("EFECTIVO")
    val metodoPago: StateFlow<String> = _metodoPago.asStateFlow()

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    fun actualizarDireccion(nuevaDireccion: String) {
        _direccion.value = nuevaDireccion
    }

    fun actualizarMetodoPago(nuevoMetodo: String) {
        _metodoPago.value = nuevoMetodo
    }

    fun realizarPedido(cartItems: List<CartEntity>) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                // Se mapean las entidades locales a ItemCheckoutDTO
                val itemsCheckout = cartItems.map { item ->
                    ItemCheckoutDTO(
                        productoId = item.productoId.toLong(),
                        cantidad = item.cantidad
                    )
                }

                // Se construye el CheckoutRequest requerido por el endpoint POST /api/pedidos/checkout
                val request = CheckoutRequest(
                    productos = itemsCheckout,
                    metodoPago = _metodoPago.value,
                    direccionEnvio = _direccion.value
                )

                // Se invoca el endpoint dedicado de checkout
                val response = RetrofitClient.apiService.procesarCheckout(request)
                val mensajeExito = response.mensaje ?: "Pedido #${response.pedidoId ?: ""} realizado con éxito"

                _checkoutState.value = CheckoutState.Success(mensajeExito)
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error(e.message ?: "Error al procesar el pedido")
            }
        }
    }

    fun resetState() {
        _checkoutState.value = CheckoutState.Idle
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val message: String) : CheckoutState()
    data class Error(val error: String) : CheckoutState()
}