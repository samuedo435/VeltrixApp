package com.example.veltrixapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.veltrixapp.data.ClienteDTO
import com.example.veltrixapp.viewmodel.ProfileState
import com.example.veltrixapp.viewmodel.ProfileViewModel
import com.example.veltrixapp.viewmodel.UpdateProfileState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onOpenDrawer: () -> Unit
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val updateState by profileViewModel.updateState.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        profileViewModel.cargarPerfil()
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateProfileState.Success -> {
                Toast.makeText(context, (updateState as UpdateProfileState.Success).message, Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateState()
            }
            is UpdateProfileState.Error -> {
                Toast.makeText(context, (updateState as UpdateProfileState.Error).error, Toast.LENGTH_LONG).show()
                profileViewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { profileViewModel.cargarPerfil() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            when (val state = profileState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { profileViewModel.cargarPerfil() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is ProfileState.Success -> {
                    val usuario = state.profileData.usuario
                    val cliente = state.profileData.cliente

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                    ) {
                        // Tarjeta 1: Resumen de Cuenta e Identidad
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Información de Cuenta",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Text("Correo: ${usuario.correo ?: "No registrado"}")
                                Text("Rol: ${usuario.rol ?: "CLIENTE"}")
                                if (usuario.id != null) {
                                    Text("ID Usuario: ${usuario.id}")
                                }
                                if (cliente?.id != null) {
                                    Text("ID Cliente: ${cliente.id}")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta 2: Formulario para Actualizar Datos Personales
                        DatosPersonalesSection(
                            cliente = cliente,
                            isUpdating = updateState is UpdateProfileState.Loading,
                            onSave = { nombre, apellido, telefono, direccion ->
                                focusManager.clearFocus()
                                val clienteId = cliente?.id ?: 0L
                                val usuarioId = usuario.id ?: 0L
                                profileViewModel.actualizarDatosPersonales(
                                    clienteId = clienteId,
                                    usuarioId = usuarioId,
                                    nombre = nombre,
                                    apellido = apellido,
                                    telefono = telefono,
                                    direccion = direccion
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta 3: Formulario para Cambiar Contraseña
                        CambiarContrasenaSection(
                            isUpdating = updateState is UpdateProfileState.Loading,
                            onUpdatePassword = { actual, nueva, confirmacion ->
                                focusManager.clearFocus()
                                profileViewModel.actualizarContrasena(
                                    usuarioId = usuario.id ?: 0L,
                                    correo = usuario.correo ?: "",
                                    rol = usuario.rol ?: "CLIENTE",
                                    passwordActual = actual,
                                    nuevaPassword = nueva,
                                    confirmarPassword = confirmacion
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatosPersonalesSection(
    cliente: ClienteDTO?,
    isUpdating: Boolean,
    onSave: (String, String, String, String) -> Unit
) {
    var nombre by remember(cliente) { mutableStateOf(cliente?.nombre ?: "") }
    var apellido by remember(cliente) { mutableStateOf(cliente?.apellido ?: "") }
    var telefono by remember(cliente) { mutableStateOf(cliente?.telefono ?: "") }
    var direccion by remember(cliente) { mutableStateOf(cliente?.direccion ?: "") }

    val apellidoFocus = remember { FocusRequester() }
    val telefonoFocus = remember { FocusRequester() }
    val direccionFocus = remember { FocusRequester() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Actualizar Datos Personales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { apellidoFocus.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { telefonoFocus.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(apellidoFocus)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { direccionFocus.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(telefonoFocus)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(direccionFocus)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSave(nombre, apellido, telefono, direccion) },
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUpdating) "Guardando..." else "Guardar Cambios Personales")
            }
        }
    }
}

@Composable
fun CambiarContrasenaSection(
    isUpdating: Boolean,
    onUpdatePassword: (String, String, String) -> Unit
) {
    var passwordActual by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var passActualVisible by remember { mutableStateOf(false) }
    var nuevaPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    val nuevaFocus = remember { FocusRequester() }
    val confirmFocus = remember { FocusRequester() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cambiar Contraseña",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = passwordActual,
                onValueChange = { passwordActual = it },
                label = { Text("Contraseña actual") },
                singleLine = true,
                visualTransformation = if (passActualVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passActualVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passActualVisible = !passActualVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { nuevaFocus.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it },
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = if (nuevaPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (nuevaPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { nuevaPassVisible = !nuevaPassVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { confirmFocus.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nuevaFocus)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it },
                label = { Text("Confirmar nueva contraseña") },
                singleLine = true,
                visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmFocus)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onUpdatePassword(passwordActual, nuevaPassword, confirmarPassword)
                },
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUpdating) "Actualizando..." else "Actualizar Contraseña")
            }
        }
    }
}