package com.sana.app.ui.student.diary

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class DiaryEntry(val id: Int, val mood: String, val content: String, val title: String, val date: String)
val MOODS = listOf("😊 Feliz" to "HAPPY", "😌 Tranquilo" to "CALM", "😐 Neutral" to "NEUTRAL", "😢 Triste" to "SAD", "😰 Ansioso" to "ANXIOUS", "😠 Enojado" to "ANGRY")

@Composable
fun DiaryScreen(userId: Long, onNavigateBack: () -> Unit, viewModel: DiaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), themeManager: com.sana.app.core.theme.ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = com.sana.app.core.theme.ThemeManager.THEME_DARK)
    val isDark = currentTheme != com.sana.app.core.theme.ThemeManager.THEME_LIGHT
    var entries by remember { mutableStateOf(listOf<DiaryEntry>()) }
    var showNewEntry by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📝 Diario Emocional", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Surface(shape = RoundedCornerShape(12.dp), color = DarkPalette.Tertiary) { Text("${entries.size}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = androidx.compose.ui.graphics.Color.White) } }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showNewEntry = !showNewEntry }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (showNewEntry) DarkPalette.Error else DarkPalette.Tertiary)) { Text(if (showNewEntry) "Cancelar" else "Nueva Entrada") }
        
        AnimatedVisibility(visible = showNewEntry, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¿Cómo te sientes?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        MOODS.forEach { (label, code) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedMood = code }) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = if (selectedMood == code) DarkPalette.Primary else DarkPalette.SurfaceVariant) { Box(contentAlignment = Alignment.Center) { Text(label.split(" ").first()) } }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Escribe tus pensamientos...") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (selectedMood.isNotEmpty() && content.isNotBlank()) {
                            entries = entries + DiaryEntry(entries.size, selectedMood, content, title.ifBlank { "Sin título" }, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
                            selectedMood = ""; content = ""; title = ""; showNewEntry = false
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = selectedMood.isNotEmpty() && content.isNotBlank()) { Text("💾 Guardar") }
                }
            }
        }
        
        if (entries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Tu diario está vacío", color = DarkPalette.TextMuted) } }
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(entries.reversed()) { entry ->
                    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row { Text(MOODS.find { it.second == entry.mood }?.first?.split(" ")?.first() ?: "😐", style = MaterialTheme.typography.headlineMedium); Spacer(Modifier.width(12.dp)); Text(entry.title, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Text(entry.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) }
                            Spacer(Modifier.height(12.dp)); Text(entry.content)
                        }
                    }
                }
            }
        }
    }
}
