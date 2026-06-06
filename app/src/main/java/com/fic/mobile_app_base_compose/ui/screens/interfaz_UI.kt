package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.fic.mobile_app_base_compose.data.local.DatabaseHelper
import com.fic.mobile_app_base_compose.data.local.ROL_DOCENTE
import com.fic.mobile_app_base_compose.data.local.Usuario

@Composable
fun interfaz_UI() {
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    var pantallaActual by remember { mutableStateOf("LOGIN") }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    val listaAvisos = remember { mutableStateListOf<Aviso>() }

    LaunchedEffect(Unit) {
        listaAvisos.clear()
        listaAvisos.addAll(db.obtenerAvisos())
    }

    if (pantallaActual == "LOGIN") {
        LoginScreen(
            validarUsuario = { matricula, password ->
                db.validarUsuario(matricula, password)
            },
            onLoginExitoso = { usuario ->
                usuarioActual = usuario
                listaAvisos.clear()
                listaAvisos.addAll(db.obtenerAvisos())
                pantallaActual = "MURO"
            }
        )
    } else {
        usuarioActual?.let { usuario ->
            MuroAvisosScreen(
                nombreUsuario = usuario.nombre,
                esDocente = usuario.rol == ROL_DOCENTE,
                listaAvisos = listaAvisos,
                onPublicarAviso = { nuevoAviso ->
                    db.guardarAviso(
                        docente = usuario.nombre,
                        materia = nuevoAviso.materia,
                        mensaje = nuevoAviso.mensaje,
                        esUrgente = nuevoAviso.esUrgente
                    )
                    listaAvisos.clear()
                    listaAvisos.addAll(db.obtenerAvisos())
                },
                onCerrarSesion = {
                    usuarioActual = null
                    pantallaActual = "LOGIN"
                }
            )
        }
    }
}
