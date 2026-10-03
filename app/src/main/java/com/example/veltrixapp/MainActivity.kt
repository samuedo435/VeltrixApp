package com.example.veltrixapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.veltrixapp.local.AppDatabase
import com.example.veltrixapp.repository.CartRepository
import com.example.veltrixapp.ui.screens.CatalogScreen
import com.example.veltrixapp.ui.theme.VeltrixAppTheme
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CartViewModelFactory
import com.example.veltrixapp.viewmodel.CatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeltrixAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VeltrixAppNavigation()
                }
            }
        }
    }
}

@Composable
fun VeltrixAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val cartRepository = remember { CartRepository(database.cartDao()) }

    val catalogViewModel: CatalogViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository)
    )

    NavHost(navController = navController, startDestination = "catalog") {

        composable("catalog") {
            CatalogScreen(
                catalogViewModel = catalogViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable("cart") {
            // Pantalla del carrito (por implementar)
        }
    }
}