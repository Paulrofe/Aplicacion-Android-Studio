package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.runtime.*

@Composable
fun interfaz_UI() {
    var pantallaActual by remember { mutableStateOf("LOGIN") }
    var nombreUsuario by remember { mutableStateOf("") }
    var esDocente by remember { mutableStateOf(false) }

    val listaAvisos = remember { mutableStateListOf<Aviso>() }

    if (pantallaActual == "LOGIN") {
        LoginScreen(
            onLoginIntent = { correo, password, rol, codigo ->
                // Lógica de validación básica
                if (correo.isNotBlank() && password.isNotBlank()) {
                    if (rol == RolUsuario.DOCENTE && codigo.isBlank()) {
                        null
                    } else {
                        AppUser(
                            nombre = correo.split("@")[0].replaceFirstChar { it.uppercase() },
                            correo = correo,
                            rol = rol
                        )
                    }
                } else {
                    null
                }
            },
            onLoginExitoso = { user ->
                nombreUsuario = user.nombre
                esDocente = user.rol == RolUsuario.DOCENTE
                pantallaActual = "MURO"
            }
        )
    } else {
        MuroAvisosScreen(
            nombreUsuario = nombreUsuario,
            esDocente = esDocente,
            listaAvisos = listaAvisos,
            onCerrarSesion = { pantallaActual = "LOGIN" }
        )
    }
}