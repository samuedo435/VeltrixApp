package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.data.DetallePedidoDTO
import com.example.veltrixapp.data.PedidoDTO
import com.example.veltrixapp.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PedidoConDetalles(
    val pedido: PedidoDTO,
    val detalles: List<DetallePedidoDTO>
)

sealed class OrdersState {
    object Loading : OrdersState()
    data class Success(val pedidos: List<PedidoConDetalles>) : OrdersState()
    data class Error(val error: String) : OrdersState()
}

class OrdersViewModel : ViewModel() {

    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Loading)
    val ordersState: StateFlow<OrdersState> = _ordersState.asStateFlow()

    init {
        cargarPedidos()
    }

    fun cargarPedidos() {
        viewModelScope.launch {
            _ordersState.value = OrdersState.Loading
            try {
                // 1. Obtener usuario autenticado actual (devuelve usuarioId ej: 11)
                val usuarioActual = try {
                    RetrofitClient.apiService.obtenerUsuarioAutenticado()
                } catch (e: Exception) {
                    null
                }

                // 2. Obtener lista de clientes para relacionar usuarioId -> clienteId (ej: usuario 11 -> cliente 7)
                val clientes = try {
                    RetrofitClient.apiService.obtenerClientes()
                } catch (e: Exception) {
                    emptyList()
                }

                val clienteActual = clientes.find { cliente ->
                    (usuarioActual?.id != null && cliente.usuarioId == usuarioActual.id) ||
                    (!usuarioActual?.correo.isNullOrBlank() && cliente.correo?.equals(usuarioActual.correo, ignoreCase = true) == true)
                }

                // 3. Obtener todos los pedidos desde el backend
                val todosLosPedidos = try {
                    RetrofitClient.apiService.obtenerPedidos()
                } catch (e: HttpException) {
                    val body = e.response()?.errorBody()?.string()
                    throw Exception("Error ${e.code()} en el servidor al consultar pedidos: ${body ?: e.message()}")
                }

                // 4. Filtrar pedidos mapeando correctamente por clienteId (ej: clienteId == 7)
                val pedidosUsuario = when {
                    clienteActual?.id != null -> {
                        val porClienteId = todosLosPedidos.filter { it.clienteId == clienteActual.id }
                        if (porClienteId.isNotEmpty()) porClienteId else todosLosPedidos
                    }
                    usuarioActual?.id != null -> {
                        val porUsuarioId = todosLosPedidos.filter { it.clienteId == usuarioActual.id }
                        if (porUsuarioId.isNotEmpty()) porUsuarioId else todosLosPedidos
                    }
                    else -> todosLosPedidos
                }

                // 5. Obtener detalles de pedidos
                val detalles = try {
                    RetrofitClient.apiService.obtenerDetallesPedido()
                } catch (e: Exception) {
                    emptyList()
                }

                // Agrupar los detalles por id del pedido
                val detallesPorPedido = detalles.groupBy { it.pedidoId }

                // 6. Combinar los pedidos con sus respectivos artículos
                val listaCombinada = pedidosUsuario.map { pedido ->
                    PedidoConDetalles(
                        pedido = pedido,
                        detalles = detallesPorPedido[pedido.id] ?: emptyList()
                    )
                }.sortedByDescending { it.pedido.id }

                _ordersState.value = OrdersState.Success(listaCombinada)
            } catch (e: Exception) {
                _ordersState.value = OrdersState.Error(e.message ?: "Error al cargar los pedidos")
            }
        }
    }
}