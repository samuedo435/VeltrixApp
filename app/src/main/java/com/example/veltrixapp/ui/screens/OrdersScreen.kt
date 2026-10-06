package com.example.veltrixapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.data.DetallePedidoDTO
import com.example.veltrixapp.data.PedidoDTO
import com.example.veltrixapp.viewmodel.OrdersState
import com.example.veltrixapp.viewmodel.OrdersViewModel
import com.example.veltrixapp.viewmodel.PedidoConDetalles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel,
    onOpenDrawer: () -> Unit
) {
    val ordersState by ordersViewModel.ordersState.collectAsState()

    // Recargar pedidos cada vez que se accede a la pantalla
    LaunchedEffect(Unit) {
        ordersViewModel.cargarPedidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { ordersViewModel.cargarPedidos() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = ordersState) {
                is OrdersState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is OrdersState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { ordersViewModel.cargarPedidos() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is OrdersState.Success -> {
                    if (state.pedidos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no has realizado ningún pedido",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.pedidos) { item ->
                                OrderCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(item: PedidoConDetalles) {
    val pedido = item.pedido
    val detalles = item.detalles

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado: ID de pedido y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                EstadoBadge(estado = pedido.estado ?: "PENDIENTE")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Fecha del pedido
            if (!pedido.fechaPedido.isNullOrBlank()) {
                val fechaFormateada = pedido.fechaPedido.take(10) // Toma YYYY-MM-DD
                Text(
                    text = "Fecha: $fechaFormateada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Lista de productos incluidos en el pedido
            if (detalles.isNotEmpty()) {
                Text(
                    text = "Productos:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                detalles.forEach { detalle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${detalle.cantidad ?: 1}x ${detalle.productoNombre ?: "Producto #${detalle.productoId ?: ""}"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${detalle.subtotal ?: 0.0}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "Detalles de productos no disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Total del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${pedido.montoTotal ?: 0.0}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EstadoBadge(estado: String) {
    val (backgroundColor, contentColor) = when (estado.uppercase()) {
        "ENTREGADO", "PAGADO" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "ENVIADO" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        "PENDIENTE" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        "CANCELADO" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color(0xFFF5F5F5), Color(0xFF616161))
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = estado.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}