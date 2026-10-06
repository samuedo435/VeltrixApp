package com.example.veltrixapp.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    cartViewModel: CartViewModel,
    onNavigateToCart: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val productosLista by catalogViewModel.productos.collectAsState()

    // Se observa el estado del carrito para calcular la cantidad del contador
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalItems = cartItems.sumOf { it.cantidad }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Veltrix") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir Menú"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        // Se agrega el componente BadgedBox para mostrar el número de productos
                        BadgedBox(
                            badge = {
                                if (totalItems > 0) {
                                    Badge {
                                        Text(totalItems.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ver Carrito"
                            )
                        }
                    }
                }
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
                        // Se muestra una confirmación visual en la parte inferior de la pantalla
                        Toast.makeText(
                            context,
                            "${producto.nombre} agregado al carrito",
                            Toast.LENGTH_SHORT
                        ).show()
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