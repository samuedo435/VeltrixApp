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
}