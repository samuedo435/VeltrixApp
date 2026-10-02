package com.example.veltrixapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- MODELOS DE DATOS ---
data class LoginRequest(val correo: String, val password: String)
data class LoginResponse(val token: String)

data class ProductoDTO(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoriaId: Long?,
    val categoriaNombre: String?
)

// --- INTERFAZ DE RETROFIT ---
interface VeltrixApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/api/productos")
    suspend fun obtenerProductos(): List<ProductoDTO>
}