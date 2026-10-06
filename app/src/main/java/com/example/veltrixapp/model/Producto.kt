package com.example.veltrixapp.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int
    // Se agregará la propiedad categoría una vez se conozca la estructura de Categoria.java
)