package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.data.ProductoDTO
import com.example.veltrixapp.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    // Se define el estado interno para la lista de productos provenientes de la API
    private val _productos = MutableStateFlow<List<ProductoDTO>>(emptyList())
    val productos: StateFlow<List<ProductoDTO>> = _productos.asStateFlow()

    // Se define un estado para manejar errores de red o servidor
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarProductos()
    }

    /**
     * Se obtienen los productos reales desde el backend mediante Retrofit.
     */
    private fun cargarProductos() {
        viewModelScope.launch {
            try {
                // Se realiza la petición HTTP al endpoint /api/productos definido en VeltrixApiService
                val listaBackend = RetrofitClient.apiService.obtenerProductos()
                _productos.value = listaBackend
                _error.value = null
            } catch (e: Exception) {
                // Se captura cualquier error de conexión o parseo para evitar que la app colapse
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }
}