package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fic.mobile_app_base_compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuroAvisosScreen(
    nombreUsuario: String,
    matriculaUsuario: String,
    esDocente: Boolean,
    listaAvisos: List<Aviso>,
    estadoSincronizacion: String,
    onSincronizar: () -> Unit,
    onPublicarAviso: (Aviso) -> Unit,
    onEliminarAviso: (Int) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var menuExpandido by remember { mutableStateOf(false) }
    var filtroActual by remember { mutableStateOf("Todos") }

    val avisosFiltrados = when (filtroActual) {
        "Urgentes" -> listaAvisos.filter { it.esUrgente }
        "Por Docente" -> listaAvisos.sortedBy { it.docente }
        "Por Materia" -> listaAvisos.sortedBy { it.materia }
        else -> listaAvisos
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpandido = true }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                        }

                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.filtro_todos), fontWeight = if (filtroActual == "Todos") FontWeight.Bold else FontWeight.Normal) },
                                onClick = { filtroActual = "Todos"; menuExpandido = false }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.filtro_urgentes), fontWeight = if (filtroActual == "Urgentes") FontWeight.Bold else FontWeight.Normal) },
                                onClick = { filtroActual = "Urgentes"; menuExpandido = false }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.agrupar_profesor), fontWeight = if (filtroActual == "Por Docente") FontWeight.Bold else FontWeight.Normal) },
                                onClick = { filtroActual = "Por Docente"; menuExpandido = false }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.agrupar_materia), fontWeight = if (filtroActual == "Por Materia") FontWeight.Bold else FontWeight.Normal) },
                                onClick = { filtroActual = "Por Materia"; menuExpandido = false }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sincronizar_api)) },
                                onClick = { onSincronizar(); menuExpandido = false }
                            )
                        }
                    }
                },
                title = {
                    Column {
                        Text(stringResource(R.string.muro_titulo), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text(nombreUsuario, fontSize = 12.sp, color = Color(0xFF00E5FF))
                    }
                },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text(stringResource(R.string.cerrar_sesion), color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2027))
            )
        },
        floatingActionButton = {
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
                matriculaDocente = matriculaUsuario,
                onDismiss = { mostrarDialogo = false },
                onPublicar = { nuevoAviso ->
                    onPublicarAviso(nuevoAviso)
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(stringResource(R.string.publicaciones_dia), fontSize = 14.sp, color = Color.Gray)
                        Text(estadoSincronizacion, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(stringResource(R.string.filtro_label, filtroActual), fontSize = 12.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
            }

            if (avisosFiltrados.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.sin_avisos), color = Color.Gray)
                    }
                }
            }

            items(avisosFiltrados) { aviso ->
                val esDuenoDelAviso = aviso.docente == nombreUsuario

                TarjetaAviso(
                    aviso = aviso,
                    esDueno = esDuenoDelAviso,
                    onEliminarClick = { onEliminarAviso(aviso.id) }
                 )
            }
        }
    }
}