package com.example.veltrixapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.veltrixapp.data.RetrofitClient
import com.example.veltrixapp.local.AppDatabase
import com.example.veltrixapp.repository.CartRepository
import com.example.veltrixapp.ui.navigation.AppNavigation
import com.example.veltrixapp.ui.theme.VeltrixAppTheme
import com.example.veltrixapp.utils.SessionManager
import com.example.veltrixapp.viewmodel.AuthViewModel
import com.example.veltrixapp.viewmodel.CartViewModel
import com.example.veltrixapp.viewmodel.CartViewModelFactory
import com.example.veltrixapp.viewmodel.CatalogViewModel
import com.example.veltrixapp.viewmodel.CheckoutViewModel
import com.example.veltrixapp.viewmodel.OrdersViewModel
import com.example.veltrixapp.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        RetrofitClient.sessionManager = sessionManager

        val startDestination = if (sessionManager.fetchAuthToken() != null) "catalog" else "login"

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "veltrix_database"
        ).build()

        val repository = CartRepository(database.cartDao())

        val cartViewModel = CartViewModelFactory(repository).create(CartViewModel::class.java)

        val authViewModel = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }.create(AuthViewModel::class.java)

        val checkoutViewModel = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CheckoutViewModel(sessionManager) as T
            }
        }.create(CheckoutViewModel::class.java)

        val catalogViewModel = CatalogViewModel()
        val ordersViewModel = OrdersViewModel()
        val profileViewModel = ProfileViewModel()

        setContent {
            VeltrixAppTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel,
                    checkoutViewModel = checkoutViewModel,
                    ordersViewModel = ordersViewModel,
                    profileViewModel = profileViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}