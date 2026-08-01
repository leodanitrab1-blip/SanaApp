package com.sana.app.ui.student.diary

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.database.entities.DiaryEntryEntity
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import com.sana.app.core.utils.toRelativeDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(userId: Long, onNavigateBack: () -> Unit, viewModel: DiaryViewModel = hiltViewModel(), themeManager: ThemeManager) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    var showNewEntry by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 40) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("📝 Diario Emocional", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { showNewEntry = true }) { Icon(Icons.Default.Add, "Nueva entrada") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))

            if (showNewEntry) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Título (opcional)") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¿Cómo te sientes?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Constants.MOODS.forEach { (code, label) ->
                            val emoji = label.split(" ").first()
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedMood = code }) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = if (selectedMood == code) (if (isDark) DarkPalette.Primary else LightPalette.Primary) else (if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant)) { Box(contentAlignment = Alignment.Center) { Text(emoji, style = MaterialTheme.typography.headlineSmall) } }
                                Text(code.take(3), style = MaterialTheme.typography.labelSmall, color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth().weight(1f), placeholder = { Text("Escribe tus pensamientos...") }, shape = RoundedCornerShape(16.dp))
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showNewEntry = false }, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                        Button(onClick = { if (selectedMood.isNotEmpty() && content.isNotBlank()) { viewModel.saveEntry(userId, selectedMood, content, title.ifBlank { null }); showNewEntry = false } }, modifier = Modifier.weight(1f), enabled = selectedMood.isNotEmpty() && content.isNotBlank()) { Text("Guardar") }
                    }
                }
            } else if (uiState.entries.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Book, null, modifier = Modifier.size(80.dp), tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("Tu diario está vacío", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { showNewEntry = true }) { Text("Nueva entrada") }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
                    items(uiState.entries) { entry ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(Constants.MOODS.find { it.first == entry.mood }?.second?.split(" ")?.first() ?: "😐", style = MaterialTheme.typography.headlineSmall)
                                    Text(entry.timestamp.toRelativeDate(), style = MaterialTheme.typography.labelSmall, color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted)
                                }
                                entry.title?.let { Text(it, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.height(4.dp))
                                Text(entry.content, style = MaterialTheme.typography.bodyMedium, maxLines = 5)
                            }
                        }
                    }
                }
            }
        }
    }
}
