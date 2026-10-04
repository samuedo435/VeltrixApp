package com.example.veltrixapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.veltrixapp.local.AppDatabase
import com.example.veltrixapp.repository.CartRepository
import com.example.veltrixapp.ui.navigation.AppNavigation
import com.example.veltrixapp.ui.theme.VeltrixAppTheme
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se inicializa la base de datos de Room
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "veltrix_database"
        ).build()

        // Se inicializa el repositorio pasándole el DAO
        val repository = CartRepository(database.cartDao())

        // Se construye el CartViewModel inyectando el repositorio
        val cartViewModel = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(repository) as T
            }
        }.create(CartViewModel::class.java)

        // Se instancia el CatalogViewModel que no requiere dependencias locales
        val catalogViewModel = CatalogViewModel()

        setContent {
            VeltrixAppTheme {
                // Se inicia el enrutador principal de la aplicación
                AppNavigation(
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}