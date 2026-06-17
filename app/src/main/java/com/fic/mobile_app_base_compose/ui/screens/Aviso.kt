package com.fic.mobile_app_base_compose.ui.screens

data class Aviso(
    val id: Int,
    val docente: String,
    val materia: String,
    val mensaje: String,
    val esUrgente: Boolean,
    val archivoUri: String? = null // NUEVO: Ruta de la imagen adjunta
)