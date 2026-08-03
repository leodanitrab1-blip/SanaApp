package com.sana.app.ui.director

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import java.text.SimpleDateFormat
import java.util.*

data class Announcement(val title: String, val content: String, val date: String, val priority: String = "NORMAL")

@Composable
fun DirectorAnnouncementsScreen(
    announcements: List<Announcement>,
    onAddAnnouncement: (String, String, String) -> Unit,
    onDeleteAnnouncement: (Int) -> Unit,
    isDark: Boolean,
    onBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newPriority by remember { mutableStateOf("NORMAL") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // CABECERA
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
            Text("📢 Avisos y Comunicados", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            // Badge de cantidad
            Surface(shape = RoundedCornerShape(12.dp), color = DarkPalette.Primary) {
                Text("${announcements.size} avisos", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // BOTÓN NUEVO AVISO
        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (showForm) DarkPalette.Error else DarkPalette.Primary)
        ) {
            Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(if (showForm) "Cancelar" else "Nuevo Aviso", fontWeight = FontWeight.Bold)
        }
        
        // FORMULARIO
        AnimatedVisibility(visible = showForm, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nuevo comunicado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newContent, onValueChange = { newContent = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Contenido del aviso") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    
                    // Prioridad
                    Text("Prioridad:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ALTA" to DarkPalette.Error, "NORMAL" to DarkPalette.Warning, "BAJA" to DarkPalette.Success).forEach { (label, color) ->
                            FilterChip(selected = newPriority == label, onClick = { newPriority = label }, label = { Text(label) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = color))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            onAddAnnouncement(newTitle, newContent, newPriority)
                            newTitle = ""; newContent = ""; newPriority = "NORMAL"; showForm = false
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Publicar Aviso 📢") }
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        // LISTA DE AVISOS
        if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Campaign, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay avisos publicados", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted, textAlign = TextAlign.Center)
                    Text("Publica tu primer aviso para los docentes", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(announcements.size) { index ->
                    val a = announcements[index]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Indicador de prioridad
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (a.priority) { "ALTA" -> DarkPalette.Error; "BAJA" -> DarkPalette.Success; else -> DarkPalette.Warning }
                                ) {}
                                Spacer(Modifier.width(8.dp))
                                Text(a.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                Text(a.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
                                IconButton(onClick = { onDeleteAnnouncement(index) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Eliminar", tint = DarkPalette.Error, modifier = Modifier.size(18.dp)) }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(a.content, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = when (a.priority) { "ALTA" -> DarkPalette.Error.copy(alpha = 0.2f); "BAJA" -> DarkPalette.Success.copy(alpha = 0.2f); else -> DarkPalette.Warning.copy(alpha = 0.2f) }) {
                                Text(a.priority, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
