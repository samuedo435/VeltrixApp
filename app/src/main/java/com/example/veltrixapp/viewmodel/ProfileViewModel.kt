package com.example.veltrixapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veltrixapp.data.AuthUserResponse
import com.example.veltrixapp.data.ClienteDTO
import com.example.veltrixapp.data.ClienteRequest
import com.example.veltrixapp.data.RetrofitClient
import com.example.veltrixapp.data.UsuarioRef
import com.example.veltrixapp.data.UsuarioUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileData(
    val usuario: AuthUserResponse,
    val cliente: ClienteDTO?
)

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profileData: ProfileData) : ProfileState()
    data class Error(val error: String) : ProfileState()
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    data class Success(val message: String) : UpdateProfileState()
    data class Error(val error: String) : UpdateProfileState()
}

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateState: StateFlow<UpdateProfileState> = _updateState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                // Obtener datos del usuario desde /api/auth/me
                val usuario = RetrofitClient.apiService.obtenerUsuarioAutenticado()

                // Obtener datos del cliente asociado
                val clientes = try {
                    RetrofitClient.apiService.obtenerClientes()
                } catch (e: Exception) {
                    emptyList()
                }

                val cliente = clientes.find { c ->
                    (usuario.id != null && c.usuarioId == usuario.id) ||
                    (!usuario.correo.isNullOrBlank() && c.correo?.equals(usuario.correo, ignoreCase = true) == true)
                }

                _profileState.value = ProfileState.Success(ProfileData(usuario, cliente))
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Error al cargar perfil: ${e.message}")
            }
        }
    }

    fun actualizarDatosPersonales(
        clienteId: Long,
        usuarioId: Long,
        nombre: String,
        apellido: String,
        telefono: String,
        direccion: String
    ) {
        if (nombre.isBlank() || apellido.isBlank()) {
            _updateState.value = UpdateProfileState.Error("El nombre y apellido no pueden estar vacíos")
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateProfileState.Loading
            try {
                val request = ClienteRequest(
                    id = clienteId,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    direccion = direccion,
                    usuario = UsuarioRef(id = usuarioId),
                    usuarioId = usuarioId
                )
                RetrofitClient.apiService.actualizarCliente(clienteId, request)
                _updateState.value = UpdateProfileState.Success("Datos personales actualizados correctamente")
                cargarPerfil()
            } catch (e: Exception) {
                _updateState.value = UpdateProfileState.Error("Error al actualizar datos: ${e.message}")
            }
        }
    }

    fun actualizarContrasena(
        usuarioId: Long,
        correo: String,
        rol: String,
        passwordActual: String,
        nuevaPassword: String,
        confirmarPassword: String
    ) {
        if (passwordActual.isBlank()) {
            _updateState.value = UpdateProfileState.Error("Ingresa tu contraseña actual")
            return
        }
        if (nuevaPassword.isBlank() || nuevaPassword.length < 6) {
            _updateState.value = UpdateProfileState.Error("La nueva contraseña debe tener al menos 6 caracteres")
            return
        }
        if (nuevaPassword != confirmarPassword) {
            _updateState.value = UpdateProfileState.Error("La nueva contraseña y la confirmación no coinciden")
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateProfileState.Loading
            try {
                val request = UsuarioUpdateRequest(
                    id = usuarioId,
                    correo = correo,
                    password = nuevaPassword,
                    rol = rol.ifBlank { "CLIENTE" }
                )
                RetrofitClient.apiService.actualizarUsuario(usuarioId, request)
                _updateState.value = UpdateProfileState.Success("Contraseña actualizada con éxito")
            } catch (e: Exception) {
                _updateState.value = UpdateProfileState.Error("Error al cambiar contraseña: ${e.message}")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateProfileState.Idle
    }
}