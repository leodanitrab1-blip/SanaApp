package com.sana.app.ui.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import java.text.SimpleDateFormat
import java.util.*

data class LogbookEntry(val id: Int, val studentName: String, val observation: String, val date: String, val category: String = "GENERAL", val mood: String = "NEUTRAL")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLogbookScreen(entries: List<LogbookEntry>, onAddEntry: (String, String, String, String) -> Unit, onDeleteEntry: (Int) -> Unit, isDark: Boolean, onBack: () -> Unit) {
    var showForm by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("GENERAL") }
    var mood by remember { mutableStateOf("NEUTRAL") }
    val categories = listOf("GENERAL", "ACADÉMICO", "CONDUCTUAL", "EMOCIONAL", "SOCIAL")
    val moods = listOf("😊 FELIZ" to "HAPPY", "😌 TRANQUILO" to "CALM", "😐 NEUTRAL" to "NEUTRAL", "😢 TRISTE" to "SAD", "😰 ANSIOSO" to "ANXIOUS")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📋 Bitácora Escolar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Surface(shape = RoundedCornerShape(12.dp), color = DarkPalette.Primary) { Text("${entries.size} registros", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.labelMedium) } }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showForm = !showForm }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (showForm) DarkPalette.Error else DarkPalette.Primary)) { Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(if (showForm) "Cancelar" else "Nueva Observación", fontWeight = FontWeight.Bold) }
        AnimatedVisibility(visible = showForm, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nueva observación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = studentName, onValueChange = { studentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del alumno") }, shape = RoundedCornerShape(12.dp)); Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = observation, onValueChange = { observation = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Observación") }, shape = RoundedCornerShape(12.dp)); Spacer(Modifier.height(8.dp))
                    Text("Categoría:", style = MaterialTheme.typography.labelMedium); Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { categories.forEach { cat -> FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) }) } }; Spacer(Modifier.height(8.dp))
                    Text("Estado del alumno:", style = MaterialTheme.typography.labelMedium); Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { moods.forEach { (label, value) -> FilterChip(selected = mood == value, onClick = { mood = value }, label = { Text(label) }) } }; Spacer(Modifier.height(16.dp))
                    Button(onClick = { if (studentName.isNotBlank() && observation.isNotBlank()) { onAddEntry(studentName, observation, category, mood); studentName = ""; observation = ""; category = "GENERAL"; mood = "NEUTRAL"; showForm = false } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Guardar Registro 📝") }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (entries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.EditNote, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted); Spacer(Modifier.height(16.dp)); Text("Bitácora vacía", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted); Text("Registra tu primera observación", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted) } } }
        else { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(entries.size) { index -> val entry = entries[index]; Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { Column(modifier = Modifier.padding(16.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Surface(modifier = Modifier.size(8.dp), shape = RoundedCornerShape(4.dp), color = when (entry.category) { "ACADÉMICO" -> DarkPalette.Primary; "CONDUCTUAL" -> DarkPalette.Warning; "EMOCIONAL" -> DarkPalette.Error; "SOCIAL" -> DarkPalette.Info; else -> DarkPalette.Secondary }) {}; Spacer(Modifier.width(8.dp)); Column(modifier = Modifier.weight(1f)) { Text("👤 ${entry.studentName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall); Text(entry.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) }; Text(when (entry.mood) { "HAPPY" -> "😊"; "CALM" -> "😌"; "SAD" -> "😢"; "ANXIOUS" -> "😰"; else -> "😐" }, style = MaterialTheme.typography.headlineSmall); IconButton(onClick = { onDeleteEntry(index) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Eliminar", tint = DarkPalette.Error, modifier = Modifier.size(18.dp)) } }; Spacer(Modifier.height(8.dp)); Text(entry.observation, style = MaterialTheme.typography.bodyMedium); Spacer(Modifier.height(4.dp)); Surface(shape = RoundedCornerShape(4.dp), color = DarkPalette.Primary.copy(alpha = 0.1f)) { Text(entry.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = DarkPalette.Primary) } } } } } }
    }
}
