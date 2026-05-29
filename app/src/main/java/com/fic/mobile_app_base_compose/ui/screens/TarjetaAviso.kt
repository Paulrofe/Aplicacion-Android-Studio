package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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