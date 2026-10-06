package com.example.veltrixapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CheckoutState
import com.example.veltrixapp.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val total by cartViewModel.totalPrice.collectAsState(initial = 0.0)
    val direccion by checkoutViewModel.direccion.collectAsState()
    val checkoutState by checkoutViewModel.checkoutState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            is CheckoutState.Success -> {
                Toast.makeText(context, (checkoutState as CheckoutState.Success).message, Toast.LENGTH_SHORT).show()
                checkoutViewModel.resetState()
                onOrderSuccess()
            }
            is CheckoutState.Error -> {
                Toast.makeText(context, (checkoutState as CheckoutState.Error).error, Toast.LENGTH_SHORT).show()
                checkoutViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Pedido") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { checkoutViewModel.realizarPedido(cartItems) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = checkoutState !is CheckoutState.Loading && cartItems.isNotEmpty() && direccion.isNotBlank()
                ) {
                    Text(if (checkoutState is CheckoutState.Loading) "Procesando..." else "Terminar pedido: $$total")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Dirección de Envío", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = direccion,
                onValueChange = { checkoutViewModel.actualizarDireccion(it) },
                label = { Text("Modificar dirección") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Resumen del Pedido", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.cantidad}x ${item.nombre}")
                        Text("$${item.precio * item.cantidad}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total a Pagar:", style = MaterialTheme.typography.titleLarge)
                Text("$$total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}