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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import com.sana.app.core.utils.toRelativeDate

/**
 * 🌿 SANA - Diario Emocional
 * 
 * Pantalla para registrar estados de ánimo y pensamientos.
 * Características:
 * - Selección de estado de ánimo con emojis
 * - Registro de pensamientos y sentimientos
 * - Historial de entradas
 * - Estadísticas de ánimo
 * - Modo privado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    var showNewEntry by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkPalette.BackgroundGradientStart,
                                DarkPalette.BackgroundGradientEnd
                            )
                        )
                    )
            )
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 40)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                LightPalette.BackgroundGradientStart,
                                LightPalette.BackgroundGradientEnd
                            )
                        )
                    )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("📝 Diario Emocional", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showNewEntry = true }) {
                        Icon(Icons.Default.Add, "Nueva entrada")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            if (showNewEntry) {
                // Formulario de nueva entrada
                NewDiaryEntry(
                    onSave = { mood, content, title ->
                        viewModel.saveEntry(userId, mood, content, title)
                        showNewEntry = false
                    },
                    onCancel = { showNewEntry = false },
                    isDark = isDark
                )
            } else if (uiState.entries.isEmpty()) {
                // Estado vacío
                EmptyDiary(isDark = isDark, onNewEntry = { showNewEntry = true })
            } else {
                // Lista de entradas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Resumen de estadísticas
                    item {
                        MoodSummaryCard(uiState = uiState, isDark = isDark)
                    }

                    items(uiState.entries) { entry ->
                        DiaryEntryCard(entry = entry, isDark = isDark, onDelete = {
                            viewModel.deleteEntry(entry.id)
                        })
                    }
                }
            }
        }
    }
}

/**
 * Formulario para nueva entrada del diario
 */
@Composable
private fun NewDiaryEntry(
    onSave: (String, String, String?) -> Unit,
    onCancel: () -> Unit,
    isDark: Boolean
) {
    var selectedMood by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Título (opcional)") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selección de estado de ánimo
        Text(
            text = "¿Cómo te sientes?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Constants.MOODS.forEach { (code, label) ->
                val moodEmoji = label.split(" ").first()
                val isSelected = selectedMood == code

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedMood = code }
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = when {
                            isSelected && isDark -> DarkPalette.Primary
                            isSelected && !isDark -> LightPalette.Primary
                            else -> if (isDark) DarkPalette.SurfaceVariant
                                   else LightPalette.SurfaceVariant
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = moodEmoji,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = code.take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("Escribe tus pensamientos y sentimientos...") },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    if (selectedMood.isNotEmpty() && content.isNotBlank()) {
                        onSave(selectedMood, content, title.ifBlank { null })
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = selectedMood.isNotEmpty() && content.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar")
            }
        }
    }
}

/**
 * Tarjeta de resumen de estados de ánimo
 */
@Composable
private fun MoodSummaryCard(
    uiState: DiaryUiState,
    isDark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f)
                            else LightPalette.Primary.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Resumen de la semana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${uiState.entries.size} entradas esta semana",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
            )
            if (uiState.predominantMood.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Estado predominante: ${
                        Constants.MOODS.find { it.first == uiState.predominantMood }?.second ?: uiState.predominantMood
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
                )
            }
        }
    }
}

/**
 * Tarjeta de entrada individual del diario
 */
@Composable
private fun DiaryEntryCard(
    entry: com.sana.app.core.database.entities.DiaryEntryEntity,
    isDark: Boolean,
    onDelete: () -> Unit
) {
    val moodEmoji = Constants.MOODS.find { it.first == entry.mood }?.second?.split(" ")?.first() ?: "😐"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = moodEmoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        entry.title?.let { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
                            )
                        }
                        Text(
                            text = entry.timestamp.toRelativeDate(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
                maxLines = 5
            )
        }
    }
}

/**
 * Estado vacío del diario
 */
@Composable
private fun EmptyDiary(isDark: Boolean, onNewEntry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Book,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu diario está vacío",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Comienza registrando cómo te sientes hoy",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNewEntry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nueva entrada")
        }
    }
}