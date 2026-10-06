package com.example.veltrixapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.veltrixapp.ui.screens.CartScreen
import com.example.veltrixapp.ui.screens.CatalogScreen
import com.example.veltrixapp.ui.screens.CheckoutScreen
import com.example.veltrixapp.ui.screens.LoginScreen
import com.example.veltrixapp.ui.screens.RegisterScreen
import com.example.veltrixapp.viewmodel.AuthViewModel
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel
import com.example.veltrixapp.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel,
    startDestination: String
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // No mostrar el drawer ni permitir gestos de abrir en login/register
    val showDrawer = currentRoute !in listOf("login", "register")

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawer,
        drawerContent = {
            if (showDrawer) {
                AppDrawerContent(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route) {
                            // Evitar múltiples copias de la misma pantalla
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        cartViewModel.clearCart() // Borra el carrito local
                        authViewModel.logout() // Borra la sesión
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true } // Limpia todo el historial de navegación
                        }
                    }
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("catalog") { popUpTo("login") { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("catalog") {
                CatalogScreen(
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel,
                    onNavigateToCart = { navController.navigate("cart") },
                    onOpenDrawer = { scope.launch { drawerState.open() } })
            }
            composable("cart") {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCheckout = { navController.navigate("checkout") }
                )
            }
            composable("checkout") {
                CheckoutScreen(
                    cartViewModel = cartViewModel,
                    checkoutViewModel = checkoutViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSuccess = {
                        cartViewModel.clearCart() // Vaciamos la BD local
                        navController.navigate("catalog") { // Redirigimos al catálogo o pedidos
                            popUpTo("catalog") { inclusive = false }
                        }
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable("orders") {
                OrdersScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onOpenDrawer: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de Perfil de Usuario")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onOpenDrawer: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de Historial de Pedidos")
        }
    }
}