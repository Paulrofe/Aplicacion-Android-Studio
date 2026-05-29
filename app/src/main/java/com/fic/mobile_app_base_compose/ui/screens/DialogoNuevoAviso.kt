package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun DialogoNuevoAviso(nombreDocente: String, onDismiss: () -> Unit, onPublicar: (Aviso) -> Unit) {
    var materia by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esUrgente by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Nuevo Aviso", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = materia, onValueChange = { materia = it }, label = { Text("Materia") })
                OutlinedTextField(value = mensaje, onValueChange = { mensaje = it }, label = { Text("Mensaje o aviso") })

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esUrgente, onCheckedChange = { esUrgente = it })
                    Text("¿Es un aviso urgente?")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (materia.isNotBlank() && mensaje.isNotBlank()) {
                        onPublicar(
                            Aviso(
                                id = (1..1000).random(),
                                docente = nombreDocente,
                                materia = materia,
                                mensaje = mensaje,
                                esUrgente = esUrgente
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2027))
            ) { Text("Publicar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}