package com.example.veltrixapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.model.Shoe
import com.example.veltrixapp.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onNavigateToCart: () -> Unit
) {
    // Observamos los estados del ViewModel. Si cambian, la UI se redibuja automáticamente.
    val shoes by viewModel.shoes.collectAsState()
    val cart by viewModel.cart.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veltrix") },
                actions = {
                    Button(
                        onClick = onNavigateToCart,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // Muestra la cantidad de items en el carrito dinámicamente
                        Text("🛒 Carrito (${cart.size})")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(shoes) { shoe ->
                ShoeCard(
                    shoe = shoe,
                    onAddToCart = { viewModel.addToCart(shoe) }
                )
            }
        }
    }
}

@Composable
fun ShoeCard(shoe: Shoe, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Espacio reservado para la imagen del producto
            Spacer(modifier = Modifier.height(100.dp))

            Text(text = shoe.name, fontWeight = FontWeight.Bold)
            Text(text = shoe.category, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$${shoe.price}", color = MaterialTheme.colorScheme.primary)

            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Añadir")
            }
        }
    }
}