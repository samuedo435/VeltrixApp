package com.example.veltrixapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.veltrixapp.ui.screens.CartScreen
import com.example.veltrixapp.ui.screens.CatalogScreen
import com.example.veltrixapp.ui.screens.LoginScreen
import com.example.veltrixapp.ui.screens.RegisterScreen
import com.example.veltrixapp.viewmodel.AuthViewModel
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("catalog") {
                        popUpTo("login") { inclusive = true }
                    }
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
                onNavigateToCart = { navController.navigate("cart") }
            )
        }
        composable("cart") {
            CartScreen(
                cartViewModel = cartViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}