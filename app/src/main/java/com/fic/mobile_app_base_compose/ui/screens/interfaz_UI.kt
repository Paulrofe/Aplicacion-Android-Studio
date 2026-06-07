package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.fic.mobile_app_base_compose.data.local.DatabaseHelper
import com.fic.mobile_app_base_compose.data.local.ROL_DOCENTE
import com.fic.mobile_app_base_compose.data.local.Usuario
import com.fic.mobile_app_base_compose.data.repository.AvisosRepository
import kotlinx.coroutines.launch

@Composable
fun interfaz_UI() {
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }
    val repository = remember { AvisosRepository(db) }
    val scope = rememberCoroutineScope()

    var pantallaActual by remember { mutableStateOf("LOGIN") }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var estadoSincronizacion by remember { mutableStateOf("Datos locales listos") }
    val listaAvisos = remember { mutableStateListOf<Aviso>() }

    fun cargarAvisosLocales() {
        listaAvisos.clear()
        listaAvisos.addAll(repository.obtenerAvisosLocales())
    }

    fun sincronizarConApi() {
        scope.launch {
            estadoSincronizacion = "Sincronizando con API externa..."
            cargarAvisosLocales()

            val sincronizado = repository.sincronizarAvisosExternos()
            cargarAvisosLocales()

            estadoSincronizacion = if (sincronizado) {
                "Sincronizado con API externa"
            } else {
                "Sin conexión: mostrando datos guardados localmente"
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarAvisosLocales()
    }

    if (pantallaActual == "LOGIN") {
        LoginScreen(
            validarUsuario = { matricula, password ->
                db.validarUsuario(matricula, password)
            },
            onLoginExitoso = { usuario ->
                usuarioActual = usuario
                cargarAvisosLocales()
                sincronizarConApi()
                pantallaActual = "MURO"
            }
        )
    } else {
        usuarioActual?.let { usuario ->
            MuroAvisosScreen(
                nombreUsuario = usuario.nombre,
                esDocente = usuario.rol == ROL_DOCENTE,
                listaAvisos = listaAvisos,
                estadoSincronizacion = estadoSincronizacion,
                onSincronizar = { sincronizarConApi() },
                onPublicarAviso = { nuevoAviso ->
                    repository.guardarAvisoLocal(
                        docente = usuario.nombre,
                        materia = nuevoAviso.materia,
                        mensaje = nuevoAviso.mensaje,
                        esUrgente = nuevoAviso.esUrgente
                    )
                    cargarAvisosLocales()
                },
                onCerrarSesion = {
                    usuarioActual = null
                    pantallaActual = "LOGIN"
                }
            )
        }
    }
}
