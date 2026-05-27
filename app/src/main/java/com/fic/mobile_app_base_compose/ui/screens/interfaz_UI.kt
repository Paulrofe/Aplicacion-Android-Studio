package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 1. MODELO DE DATOS (Estructura del Aviso)
// ==========================================
data class Aviso(
    val id: Int,
    val docente: String,
    val materia: String,
    val mensaje: String,
    val esUrgente: Boolean
)

// ==========================================
// 2. CONTROLADOR PRINCIPAL DE PANTALLAS
// ==========================================
@Composable
fun interfaz_UI() {
    // Controlan en qué pantalla estamos y quién entró
    var pantallaActual by remember { mutableStateOf("LOGIN") } // Puede ser: "LOGIN" o "MURO"
    var nombreUsuario by remember { mutableStateOf("") }
    var esDocente by remember { mutableStateOf(false) }

    // Lista global que almacena los avisos dinámicos en tiempo real
    val listaAvisos = remember { mutableStateListOf<Aviso>() }

    if (pantallaActual == "LOGIN") {
        LoginScreen(
            onLoginExitoso = { nombre, esProfesor ->
                nombreUsuario = nombre
                esDocente = esProfesor
                pantallaActual = "MURO" // Cambia de pantalla dinámicamente
            }
        )
    } else {
        MuroAvisosScreen(
            nombreUsuario = nombreUsuario,
            esDocente = esDocente,
            listaAvisos = listaAvisos,
            onCerrarSesion = { pantallaActual = "LOGIN" }
        )
    }
}

// ==========================================
// 3. INTERFAZ DE INICIO DE SESIÓN
// ==========================================
@Composable
fun LoginScreen(onLoginExitoso: (String, Boolean) -> Unit) {
    var txtNombre by remember { mutableStateOf("") }
    var switchDocente by remember { mutableStateOf(false) }

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
            value = txtNombre,
            onValueChange = { txtNombre = it },
            label = { Text("Nombre o Matrícula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Rol: Alumno o Docente
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

        Button(
            onClick = {
                if (txtNombre.isNotBlank()) {
                    onLoginExitoso(txtNombre, switchDocente)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2027))
        ) {
            Text("INGRESAR", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 4. INTERFAZ DEL MURO DE AVISOS
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuroAvisosScreen(
    nombreUsuario: String,
    esDocente: Boolean,
    listaAvisos: List<Aviso>,
    onCerrarSesion: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Muro de Avisos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text("Usuario: $nombreUsuario", fontSize = 12.sp, color = Color(0xFF00E5FF))
                    }
                },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2027))
            )
        },
        floatingActionButton = {
            // Solo el docente puede ver el botón para redactar un aviso
            if (esDocente) {
                FloatingActionButton(
                    onClick = { mostrarDialogo = true },
                    containerColor = Color(0xFF0F2027),
                    contentColor = Color(0xFF00E5FF)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Añadir")
                }
            }
        }
    ) { paddingValues ->

        if (mostrarDialogo) {
            DialogoNuevoAviso(
                nombreDocente = nombreUsuario,
                onDismiss = { mostrarDialogo = false },
                onPublicar = { nuevoAviso ->
                    (listaAvisos as MutableList).add(0, nuevoAviso) // Inserta al inicio en tiempo real
                    mostrarDialogo = false
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF4F7FA)).padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Publicaciones del día", fontSize = 14.sp, color = Color.Gray)
            }

            if (listaAvisos.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No hay avisos publicados todavía.", color = Color.Gray)
                    }
                }
            }

            items(listaAvisos) { aviso ->
                TarjetaAviso(aviso = aviso)
            }
        }
    }
}

// ==========================================
// 5. VENTANA EMERGENTE PARA REDACTAR AVISO
// ==========================================
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
                                docente = nombreDocente, // Captura automáticamente el nombre del Login
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

// ==========================================
// 6. DISEÑO DE LA TARJETA DE AVISO
// ==========================================
@Composable
fun TarjetaAviso(aviso: Aviso) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(aviso.docente, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(aviso.materia, fontSize = 12.sp, color = Color.Gray)
                }
                if (aviso.esUrgente) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("URGENTE", color = Color.Red, fontSize = 10.sp) },
                        border = BorderStroke(1.dp, Color.Red)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(aviso.mensaje, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Publicado hoy", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}