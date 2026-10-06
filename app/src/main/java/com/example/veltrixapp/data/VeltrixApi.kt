package com.example.veltrixapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- MODELOS DE DATOS Y REQUESTS ---
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val telefono: String?,
    val direccion: String?
)

data class RegisterResponse(
    val mensaje: String
)

data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class ProductoDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoriaId: Int?,
    val categoriaNombre: String?
)

data class AuthUserResponse(
    val id: Long?,
    val correo: String?,
    val rol: String?
)

data class ItemCheckoutDTO(
    val productoId: Long,
    val cantidad: Int
)

data class CheckoutRequest(
    val productos: List<ItemCheckoutDTO>,
    val metodoPago: String = "EFECTIVO",
    val direccionEnvio: String
)

data class CheckoutResponse(
    val pedidoId: Long?,
    val mensaje: String?
)

data class PedidoDTO(
    val id: Long = 0L,
    val fechaPedido: String? = null,
    val montoTotal: Double? = 0.0,
    val estado: String? = null,
    val clienteId: Long? = null,
    val nombreCliente: String? = null
)

data class DetallePedidoDTO(
    val id: Long = 0L,
    val cantidad: Int? = 1,
    val subtotal: Double? = 0.0,
    val pedidoId: Long? = null,
    val productoId: Long? = null,
    val productoNombre: String? = null
)

data class ClienteDTO(
    val id: Long? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val usuarioId: Long? = null,
    val correo: String? = null
)

// --- INTERFAZ DE RETROFIT ---
interface VeltrixApiService {

    @POST("api/auth/login")
    suspend fun iniciarSesion(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun registrarUsuario(@Body request: RegisterRequest): RegisterResponse

    @GET("api/auth/me")
    suspend fun obtenerUsuarioAutenticado(): AuthUserResponse

    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoDTO>

    @POST("api/pedidos/checkout")
    suspend fun procesarCheckout(@Body request: CheckoutRequest): CheckoutResponse

    @GET("api/pedidos")
    suspend fun obtenerPedidos(): List<PedidoDTO>

    @GET("api/detalles-pedido")
    suspend fun obtenerDetallesPedido(): List<DetallePedidoDTO>

    @GET("api/clientes")
    suspend fun obtenerClientes(): List<ClienteDTO>
}