package com.example.veltrixapp.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.R
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel
) {
    val productosLista by catalogViewModel.productos.collectAsState()

    // Se obtiene el contexto para la resolución dinámica de imágenes
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Veltrix") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosLista) { producto ->
                ProductCard(
                    nombre = producto.nombre,
                    precio = producto.precio,
                    context = context,
                    onAddToCart = {
                        cartViewModel.addProduct(
                            id = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    nombre: String,
    precio: Double,
    context: Context,
    onAddToCart: () -> Unit
) {
    // Se calcula el recurso de la imagen en tiempo real
    val imageResId = getDrawableIdFromName(context, nombre)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Se inserta la imagen del producto
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de $nombre",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$$precio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(onClick = onAddToCart) {
                Text("Agregar")
            }
        }
    }
}

/**
 * Se normaliza el nombre del producto para buscarlo en la carpeta drawable.
 * Retorna un placeholder en caso de no encontrar coincidencia.
 */
fun getDrawableIdFromName(context: Context, nombre: String): Int {
    val formattedName = nombre.lowercase().replace(" ", "_").replace("-", "_")

    val resourceId = context.resources.getIdentifier(
        formattedName,
        "drawable",
        context.packageName
    )

    return if (resourceId != 0) resourceId else R.drawable.ic_placeholder
}