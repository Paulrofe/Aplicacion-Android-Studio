package com.fic.mobile_app_base_compose.ui.screens

import com.fic.mobile_app_base_compose.ui.screens.AppUser
import com.fic.mobile_app_base_compose.ui.screens.Aviso
import com.fic.mobile_app_base_compose.ui.screens.DialogoNuevoAviso
import com.fic.mobile_app_base_compose.ui.screens.LoginScreen
import com.fic.mobile_app_base_compose.ui.screens.TarjetaAviso

enum class RolUsuario {
    ALUMNO,
    DOCENTE
}

data class AppUser(
    val nombre: String,
    val correo: String,
    val rol: RolUsuario
)