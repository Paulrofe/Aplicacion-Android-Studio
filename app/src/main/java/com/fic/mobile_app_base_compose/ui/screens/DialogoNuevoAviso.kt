package com.fic.mobile_app_base_compose.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fic.mobile_app_base_compose.R

// 1. NUEVO: Diccionario de materias por docente
private val materiasPorDocente = mapOf(
    "M001" to listOf(
        "LÓGICA DE PROGRAMACIÓN Y PENSAMIENTO COMPUTACIONAL",
        "DESARROLLO WEB DEL LADO CLIENTE"
    ),
    "M002" to listOf(
        "FUNDAMENTOS DE BASE DE DATOS",
        "MANEJADORES Y LENGUAJES DE CONSULTA DE DATOS",
        "MINERÍA DE DATOS"
    ),
    "M003" to listOf(
        "ARQUITECTURA DE COMPUTADORAS",
        "REDES DE COMPUTADORAS",
        "ADMINISTRACIÓN DE REDES"
    ),
    "M004" to listOf(
        "MATEMÁTICAS DISCRETAS",
        "ÁLGEBRA LINEAL",
        "PROBABILIDAD Y ESTADÍSTICA"
    ),
    "M005" to listOf(
        "PROGRAMACIÓN ESTRUCTURADA",
        "ESTRUCTURA DE DATOS",
        "PARADIGMAS DE PROGRAMACIÓN"
    ),
    "M006" to listOf(
        "ANÁLISIS Y DISEÑO DE SOFTWARE",
        "INGENIERÍA DE SOFTWARE",
        "MODELOS DE CALIDAD DE SOFTWARE"
    )
)

// 2. NUEVO: Diccionario para saber a qué periodo pertenece cada materia
private val periodoPorMateria = mapOf(
    "LÓGICA DE PROGRAMACIÓN Y PENSAMIENTO COMPUTACIONAL" to 1,
    "ARQUITECTURA DE COMPUTADORAS" to 1,
    "MATEMÁTICAS DISCRETAS" to 1,
    "FUNDAMENTOS DE REDES" to 1,
    "INTRODUCCIÓN A LA INFORMÁTICA" to 1,
    "LABORATORIO DE SISTEMAS OPERATIVOS" to 1,

    "PROGRAMACIÓN ESTRUCTURADA" to 2,
    "FUNDAMENTOS DE BASE DE DATOS" to 2,
    "ÁLGEBRA LINEAL" to 2,
    "REDES DE COMPUTADORAS" to 2,
    "DISEÑO DE INTERFACES DE USUARIOS" to 2,
    "ENTORNOS DE DESARROLLO PARA PROGRAMACIÓN" to 2,

    "MATEMÁTICAS APLICADAS" to 3,
    "DESARROLLO WEB LADO DEL SERVIDOR" to 3,
    "ESTRUCTURA DE DATOS" to 3,
    "ADMINISTRACIÓN DE REDES" to 3,
    "MANEJADORES Y LENGUAJES DE CONSULTA DE DATOS" to 3,
    "ANÁLISIS Y DISEÑO DE SOFTWARE" to 3,

    "BASES DE DATOS EMERGENTES" to 4,
    "DESARROLLO E IMPLEMENTACIÓN DE SISTEMAS" to 4,
    "DESARROLLO WEB DEL LADO CLIENTE" to 4,
    "PROBABILIDAD Y ESTADÍSTICA" to 4,
    "PARADIGMAS DE PROGRAMACIÓN" to 4,
    "CIBERSEGURIDAD" to 4,

    "INGENIERÍA DE SOFTWARE" to 5,
    "MINERÍA DE DATOS" to 5,
    "CÓMPUTO EN LA NUBE" to 5,
    "INTRODUCCIÓN A LA INVESTIGACIÓN EN INFORMÁTICA" to 5,
    "FUNDAMENTOS DE INTELIGENCIA ARTIFICIAL" to 5,
    "PRUEBAS DE SOFTWARE" to 5,

    "APRENDIZAJE AUTOMÁTICO" to 6,
    "DESARROLLO, SEGURIDAD Y OPERACIÓN DE SISTEMAS" to 6,
    "PROGRAMACIÓN PARA PROCESAMIENTO DE DATOS" to 6,
    "MODELOS DE CALIDAD DE SOFTWARE" to 6,
    "INNOVACIONES TECNOLÓGICAS" to 6,
    "CÓMPUTO MÓVIL" to 6
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNuevoAviso(
    nombreDocente: String,
    matriculaDocente: String,
    onDismiss: () -> Unit,
    onPublicar: (Aviso) -> Unit
) {
    val context = LocalContext.current
    val sinMateriasTexto = stringResource(id = R.string.sin_materias)
    val listaMateriasDelDocente = materiasPorDocente[matriculaDocente] ?: emptyList()
    var materiaSeleccionada by remember { mutableStateOf(listaMateriasDelDocente.firstOrNull() ?: sinMateriasTexto) }
    var materiaExpandida by remember { mutableStateOf(false) }

    // NUEVO: Lógica para calcular grupos basados en la materia seleccionada
    val periodoActual = periodoPorMateria[materiaSeleccionada] ?: 1
    val gruposDisponibles = listOf("$periodoActual-1", "$periodoActual-2", "$periodoActual-3")

    var grupoSeleccionado by remember { mutableStateOf(gruposDisponibles.first()) }
    var grupoExpandido by remember { mutableStateOf(false) }

    // NUEVO: Efecto para resetear el grupo seleccionado si el usuario cambia de materia
    LaunchedEffect(materiaSeleccionada) {
        val nuevoPeriodo = periodoPorMateria[materiaSeleccionada] ?: 1
        grupoSeleccionado = "$nuevoPeriodo-1"
    }

    var mensaje by remember { mutableStateOf("") }
    var esUrgente by remember { mutableStateOf(false) }

    // NUEVO: Estado para guardar la URI del archivo seleccionado
    var uriArchivo by remember { mutableStateOf<Uri?>(null) }

    // NUEVO: Lanzador de la galería/explorador de archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uriArchivo = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.publicar_aviso_titulo), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 1. NUEVO: Selector de Grupo
                ExposedDropdownMenuBox(
                    expanded = grupoExpandido,
                    onExpandedChange = { grupoExpandido = !grupoExpandido }
                ) {
                    OutlinedTextField(
                        value = grupoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.grupo_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = grupoExpandido) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = grupoExpandido,
                        onDismissRequest = { grupoExpandido = false }
                    ) {
                        gruposDisponibles.forEach { grupo ->
                            DropdownMenuItem(
                                text = { Text(text = "${stringResource(id = R.string.grupo_label)} $grupo") },
                                onClick = {
                                    grupoSeleccionado = grupo
                                    grupoExpandido = false
                                }
                            )
                        }
                    }
                }

                // 2. Selector de Materia (Exclusivo del profesor)
                ExposedDropdownMenuBox(
                    expanded = materiaExpandida,
                    onExpandedChange = {
                        if (listaMateriasDelDocente.isNotEmpty()) {
                            materiaExpandida = !materiaExpandida
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = materiaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.materia_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = materiaExpandida) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = materiaExpandida,
                        onDismissRequest = { materiaExpandida = false }
                    ) {
                        listaMateriasDelDocente.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(text = materia) },
                                onClick = {
                                    materiaSeleccionada = materia
                                    materiaExpandida = false
                                }
                            )
                        }
                    }
                }

                // 3. Área de Mensaje
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text(stringResource(id = R.string.mensaje_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Checkbox de Urgencia
                // NUEVO: Botón para adjuntar archivo
                OutlinedButton(
                    onClick = { filePickerLauncher.launch("image/*") }, // Usa "*/*" si quieres permitir PDFs o documentos
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AddCircle, contentDescription = "Adjuntar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uriArchivo == null) 
                            stringResource(id = R.string.adjuntar_imagen) 
                        else 
                            stringResource(id = R.string.imagen_seleccionada)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esUrgente, onCheckedChange = { esUrgente = it })
                    Text(stringResource(id = R.string.pregunta_urgente))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mensaje.isNotBlank() && listaMateriasDelDocente.isNotEmpty()) {

                        val rutaLocalGuardada = uriArchivo?.let { uri ->
                            guardarImagenInternamente(context, uri)
                        }

                        onPublicar(
                            Aviso(
                                id = 0,
                                docente = nombreDocente,
                                materia = "$materiaSeleccionada (${context.getString(R.string.grupo_label)} $grupoSeleccionado)",
                                mensaje = mensaje,
                                esUrgente = esUrgente,
                                archivoUri = rutaLocalGuardada // Guardamos la ruta permanente
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2027))
            ) { Text(stringResource(id = R.string.btn_publicar)) }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { 
                Text(stringResource(id = R.string.btn_cancelar)) 
            } 
        }
    )
}

fun guardarImagenInternamente(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        // Creamos un nombre único para la imagen
        val fileName = "aviso_img_${System.currentTimeMillis()}.jpg"
        // La guardamos en la carpeta privada de la app
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        // Devolvemos la ruta de la imagen guardada en la memoria interna
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}
