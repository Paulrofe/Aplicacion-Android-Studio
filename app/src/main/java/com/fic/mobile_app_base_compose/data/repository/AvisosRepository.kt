package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.DatabaseHelper
import com.fic.mobile_app_base_compose.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AvisosRepository(
    private val databaseHelper: DatabaseHelper
) {
    fun obtenerAvisosLocales() = databaseHelper.obtenerAvisos()

    fun guardarAvisoLocal(
        docente: String,
        materia: String,
        mensaje: String,
        esUrgente: Boolean,
        archivoUri: String? = null // NUEVO PARÁMETRO
    ) {
        databaseHelper.guardarAviso(
            docente = docente,
            materia = materia,
            mensaje = mensaje,
            esUrgente = esUrgente,
            archivoUri = archivoUri // NUEVO PARAMETRO
        )
    }

    // CAMBIO: Añadimos la función puente para eliminar avisos locales
    fun eliminarAvisoLocal(id: Int): Boolean {
        return databaseHelper.eliminarAviso(id)
    }

    suspend fun sincronizarAvisosExternos(): Boolean = withContext(Dispatchers.IO) {
        try {
            val avisosApi = RetrofitClient.api.obtenerAvisosExternos()
            databaseHelper.guardarAvisosExternos(avisosApi.take(1))
            true
        } catch (e: Exception) {
            false
        }
    }
}
