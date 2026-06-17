package com.fic.mobile_app_base_compose.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.data.local.DatabaseHelper
import com.fic.mobile_app_base_compose.data.local.ROL_DOCENTE
import com.fic.mobile_app_base_compose.data.local.Usuario
import com.fic.mobile_app_base_compose.data.repository.AvisosRepository
import com.fic.mobile_app_base_compose.ui.screens.Aviso
import kotlinx.coroutines.launch

class AvisosViewModel(context: Context) : ViewModel() {

    private val db = DatabaseHelper(context)
    private val repository = AvisosRepository(db)

    var pantallaActual = mutableStateOf("LOGIN")
        private set

    var usuarioActual = mutableStateOf<Usuario?>(null)
        private set

    var estadoSincronizacion = mutableStateOf(context.getString(R.string.estado_listo))
        private set

    val listaAvisos = mutableStateListOf<Aviso>()

    init {
        cargarAvisosLocales()
    }

    fun validarCredenciales(matricula: String, password: String): Usuario? {
        return db.validarUsuario(matricula, password)
    }

    fun iniciarSesion(usuario: Usuario, context: Context) {
        usuarioActual.value = usuario
        cargarAvisosLocales()
        sincronizarConApi(context)
        pantallaActual.value = "MURO"
    }

    fun cerrarSesion() {
        usuarioActual.value = null
        pantallaActual.value = "LOGIN"
    }

    fun cargarAvisosLocales() {
        listaAvisos.clear()
        listaAvisos.addAll(repository.obtenerAvisosLocales())
    }

    fun sincronizarConApi(context: Context) {
        viewModelScope.launch {
            estadoSincronizacion.value = context.getString(R.string.estado_sincronizando)
            cargarAvisosLocales()

            val sincronizado = repository.sincronizarAvisosExternos()
            cargarAvisosLocales()

            estadoSincronizacion.value = if (sincronizado) {
                context.getString(R.string.estado_sincronizado)
            } else {
                context.getString(R.string.estado_offline)
            }
        }
    }

    fun publicarAviso(nuevoAviso: Aviso) {
        val usuario = usuarioActual.value ?: return
        repository.guardarAvisoLocal(
            docente = usuario.nombre,
            materia = nuevoAviso.materia,
            mensaje = nuevoAviso.mensaje,
            esUrgente = nuevoAviso.esUrgente,
            archivoUri = nuevoAviso.archivoUri
        )
        cargarAvisosLocales()
    }

    fun eliminarAviso(idAviso: Int) {
        repository.eliminarAvisoLocal(idAviso)
        cargarAvisosLocales()
    }
}