package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.fic.mobile_app_base_compose.data.local.ROL_DOCENTE
import com.fic.mobile_app_base_compose.ui.viewmodel.AvisosViewModel

@Composable
fun interfaz_UI() {
    val context = LocalContext.current
    // Inicializamos el ViewModel que gestionará el estado del flujo de la app
    val viewModel = remember { AvisosViewModel(context) }

    val pantallaActual by viewModel.pantallaActual
    val usuarioActual by viewModel.usuarioActual
    val estadoSincronizacion by viewModel.estadoSincronizacion
    val listaAvisos = viewModel.listaAvisos

    if (pantallaActual == "LOGIN") {
        LoginScreen(
            validarUsuario = { matricula, password ->
                viewModel.validarCredenciales(matricula, password)
            },
            onLoginExitoso = { usuario ->
                viewModel.iniciarSesion(usuario, context)
            }
        )
    } else {
        usuarioActual?.let { usuario ->
            MuroAvisosScreen(
                nombreUsuario = usuario.nombre,
                matriculaUsuario = usuario.matricula,
                esDocente = usuario.rol == ROL_DOCENTE,
                listaAvisos = listaAvisos,
                estadoSincronizacion = estadoSincronizacion,
                onSincronizar = { viewModel.sincronizarConApi(context) },
                onPublicarAviso = { nuevoAviso ->
                    viewModel.publicarAviso(nuevoAviso)
                },
                onEliminarAviso = { idAviso ->
                    viewModel.eliminarAviso(idAviso)
                },
                onCerrarSesion = {
                    viewModel.cerrarSesion()
                }
            )
        }
    }
}