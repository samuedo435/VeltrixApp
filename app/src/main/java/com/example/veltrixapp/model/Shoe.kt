package com.example.veltrixapp.model

/**
 * Representa un producto en el catálogo de Veltrix.
 * Se utiliza 'data class' en Kotlin para generar automáticamente
 * métodos útiles como toString(), equals() y copy().
 */
data class Shoe(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String = "" // Preparado para conectar las imágenes
)