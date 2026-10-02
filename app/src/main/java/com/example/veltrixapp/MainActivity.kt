package com.example.veltrixapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.veltrixapp.ui.screens.CatalogScreen
import com.example.veltrixapp.ui.theme.VeltrixAppTheme
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
    // Instanciamos el ViewModel aquí para que comparta el estado entre pantallas si es necesario
    val catalogViewModel: CatalogViewModel = viewModel()

    NavHost(navController = navController, startDestination = "catalog") {

        composable("catalog") {
            CatalogScreen(
                viewModel = catalogViewModel,
                onNavigateToCart = { navController.navigate("cart") }
            )
        }

        composable("cart") {
            // Pantalla del carrito (por implementar)
        }
    }
}