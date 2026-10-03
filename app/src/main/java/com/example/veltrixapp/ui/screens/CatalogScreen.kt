package com.example.veltrixapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel
) {
    // Se observa el estado de los productos provenientes de la API
    // Nota: Asegurar que 'productos' sea el nombre de la variable expuesta en CatalogViewModel
    val productosLista by catalogViewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Veltrix") }
            )
        }
    ) { paddingValues ->
        // Se muestra la lista de productos utilizando LazyColumn para optimizar la memoria
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosLista) { producto ->
                ProductCard(
                    // Se asume que el modelo de datos de Retrofit tiene id, nombre y precio
                    nombre = producto.nombre,
                    precio = producto.precio,
                    onAddToCart = {
                        // Se delega el guardado local a Room mediante el CartViewModel
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
    onAddToCart: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                // Se renderiza el nombre y el precio del producto
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