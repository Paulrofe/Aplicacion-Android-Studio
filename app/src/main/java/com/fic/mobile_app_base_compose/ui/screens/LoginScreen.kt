package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.data.local.Usuario

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    validarUsuario: (String, String) -> Usuario?
) {
    var matricula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FA))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.facultad_titulo),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2027)
        )
        Text(
            text = stringResource(id = R.string.muro_subtitulo),
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = stringResource(id = R.string.inicio_instruccion),
            fontSize = 13.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it.uppercase(); error = "" },
            label = { Text(stringResource(id = R.string.matricula_label)) },
            placeholder = { Text("") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = "" },
            label = { Text(stringResource(id = R.string.password_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error, color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val usuario = validarUsuario(matricula, password)
                if (usuario != null) {
                    onLoginExitoso(usuario)
                } else {
                    error = context.getString(R.string.error_credenciales)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2027))
        ) {
            Text(
                text = stringResource(id = R.string.btn_iniciar_sesion),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
