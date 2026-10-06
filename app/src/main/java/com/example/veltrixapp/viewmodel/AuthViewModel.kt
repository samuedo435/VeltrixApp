package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.data.LoginRequest
import com.example.veltrixapp.data.RegisterRequest
import com.example.veltrixapp.data.RetrofitClient
import com.example.veltrixapp.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(correo: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.iniciarSesion(LoginRequest(correo, pass))
                sessionManager.saveAuthToken(response.token)
                _authState.value = AuthState.Success("Login exitoso")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Credenciales inválidas o error de red")
            }
        }
    }

    fun register(nombre: String, apellido: String, correo: String, pass: String, tel: String, dir: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val req = RegisterRequest(nombre, apellido, correo, pass, tel, dir)
                val response = RetrofitClient.apiService.registrarUsuario(req)
                _authState.value = AuthState.Success(response.mensaje)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al registrar: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        sessionManager.clearSession()
        resetState() // Regresa el estado a Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}