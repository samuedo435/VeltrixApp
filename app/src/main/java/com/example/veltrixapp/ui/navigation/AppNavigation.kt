package com.example.veltrixapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.veltrixapp.ui.screens.CartScreen
import com.example.veltrixapp.ui.screens.CatalogScreen
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

@Composable
fun AppNavigation(
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "catalog") {
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