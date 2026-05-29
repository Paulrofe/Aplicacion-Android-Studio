package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginIntent: (String, String, com.fic.mobile_app_base_compose.ui.screens.RolUsuario, String) -> com.fic.mobile_app_base_compose.ui.screens.AppUser?,
    onLoginExitoso: (AppUser) -> Unit
) {
    var txtCodigoDocente by remember { mutableStateOf("") }
    var txtCorreo by remember { mutableStateOf("") }
    var txtPassword by remember { mutableStateOf("") }
    var switchDocente by remember { mutableStateOf(false) }
    var ErrorMensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FA))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FACULTAD DE INFORMÁTICA", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2027))
        Text("Portal de Accesos", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = txtCorreo,
            onValueChange = { txtCorreo = it; ErrorMensaje = null },
            label = { Text("Correo Institucional (@info.uas.edu.mx)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = txtPassword,
            onValueChange = { txtPassword = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (switchDocente) "Ingresar como: DOCENTE 👨‍🏫" else "Ingresar como: ALUMNO 👨‍🎓",
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = switchDocente,
                onCheckedChange = { switchDocente = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (switchDocente) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = txtCodigoDocente,
                onValueChange = { txtCodigoDocente = it; ErrorMensaje = null },
                label = { Text("Codigo de Seguridad Docente") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        ErrorMensaje?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val rol = if (switchDocente) com.fic.mobile_app_base_compose.ui.screens.RolUsuario.DOCENTE else com.fic.mobile_app_base_compose.ui.screens.RolUsuario.ALUMNO

                val usuarioLogueado = onLoginIntent(txtCorreo, txtPassword, rol, txtCodigoDocente)

                if (usuarioLogueado != null) {
                    onLoginExitoso(usuarioLogueado)
                } else {
                    ErrorMensaje = "Correo o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2027))
        ) {
            Text("INGRESAR", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}