package com.sana.app.ui.primary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

data class PrimaryTeacherService(val title: String, val icon: ImageVector, val description: String, val gradientColors: List<androidx.compose.ui.graphics.Color>, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTeacherScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    val services = listOf(
        PrimaryTeacherService("Grupos", Icons.Default.GroupWork, "Crear grupos con padres", listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant)) { },
        PrimaryTeacherService("Anuncios", Icons.Default.Campaign, "Publicar anuncios", listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant)) { },
        PrimaryTeacherService("Tareas", Icons.Default.Assignment, "Muro de tareas", listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer)) { },
        PrimaryTeacherService("Fichas", Icons.Default.Stars, "Economía de fichas", listOf(DarkPalette.MoodHappy, DarkPalette.Warning)) { },
        PrimaryTeacherService("Actividades", Icons.Default.SelfImprovement, "Actividades psicológicas", listOf(DarkPalette.Info, DarkPalette.InfoContainer)) { },
        PrimaryTeacherService("Padres", Icons.Default.FamilyRestroom, "Gestionar padres", listOf(DarkPalette.MoodCalm, DarkPalette.Success)) { }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 60) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("🧒 Panel Primaria", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))

            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(services) { service ->
                    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = service.onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = service.gradientColors)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(service.icon, service.title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White)
                                Spacer(Modifier.height(8.dp))
                                Text(service.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(4.dp))
                                Text(service.description, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
