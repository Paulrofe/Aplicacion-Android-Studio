package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fic.mobile_app_base_compose.R

@Composable
fun TarjetaAviso(
    aviso: Aviso,
    esDueno: Boolean,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        // Si el aviso es de quien está mirando la pantalla, le ponemos un contorno azul sutil
        border = if (esDueno) BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)) else null,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(aviso.docente, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    }
                    Text(aviso.materia, fontSize = 12.sp, color = Color.Gray)
                }

                if (aviso.id > 0 && esDueno) {
                    IconButton(onClick = onEliminarClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            tint = Color.Red,
                            contentDescription = "Eliminar Aviso"
                        )
                    }
                }

                if (aviso.esUrgente) {
                    Spacer(modifier = Modifier.width(4.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.tag_urgente), color = Color.Red, fontSize = 10.sp) },
                        border = BorderStroke(1.dp, Color.Red)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(aviso.mensaje, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (aviso.archivoUri != null) {
                AsyncImage(
                    model = aviso.archivoUri,
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.publicado_hoy), fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}