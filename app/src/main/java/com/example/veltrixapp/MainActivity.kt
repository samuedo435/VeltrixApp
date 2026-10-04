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
import com.example.veltrixapp.utils.SessionManager
import com.example.veltrixapp.viewmodel.AuthViewModel
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val startDestination = if (sessionManager.fetchAuthToken() != null) "catalog" else "login"

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "veltrix_database"
        ).build()

        val repository = CartRepository(database.cartDao())

        val cartViewModel = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(repository) as T
            }
        }.create(CartViewModel::class.java)

        val authViewModel = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }.create(AuthViewModel::class.java)

        val catalogViewModel = CatalogViewModel()

        setContent {
            VeltrixAppTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}