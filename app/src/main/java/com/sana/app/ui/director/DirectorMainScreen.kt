package com.sana.app.ui.director

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

data class DirectorMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val color1: androidx.compose.ui.graphics.Color,
    val color2: androidx.compose.ui.graphics.Color,
    val badge: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorMainScreen(
    teacherCount: Int,
    announcementCount: Int,
    isDark: Boolean,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
    directorName: String = "Director"
) {
    val menuItems = listOf(
        DirectorMenuItem("announcements", "📢 Avisos", Icons.Default.Campaign, "Publicar anuncios", DarkPalette.Primary, DarkPalette.PrimaryVariant, "$announcementCount"),
        DirectorMenuItem("register_teacher", "➕ Registrar", Icons.Default.PersonAdd, "Nuevo docente", DarkPalette.Secondary, DarkPalette.SecondaryVariant),
        DirectorMenuItem("view_teachers", "👨‍🏫 Docentes", Icons.Default.Group, "Gestionar equipo", DarkPalette.Tertiary, DarkPalette.TertiaryContainer, "$teacherCount"),
        DirectorMenuItem("reports", "📊 Reportes", Icons.Default.Assessment, "Estadísticas", DarkPalette.Info, DarkPalette.InfoContainer),
        DirectorMenuItem("logbook", "📋 Bitácora", Icons.Default.HistoryEdu, "Registro diario", DarkPalette.MoodCalm, DarkPalette.Success),
        DirectorMenuItem("school_data", "🏫 Mi Escuela", Icons.Default.School, "Información", DarkPalette.MoodHappy, DarkPalette.Warning)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // CABECERA ELEGANTE
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) else LightPalette.Primary.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del director
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = DarkPalette.Primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(directorName.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("👔 $directorName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
                    Text("Panel de Dirección", style = MaterialTheme.typography.bodyMedium, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Group, null, modifier = Modifier.size(16.dp), tint = DarkPalette.Primary); Spacer(Modifier.width(4.dp)); Text("$teacherCount docentes", style = MaterialTheme.typography.labelSmall) }
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Campaign, null, modifier = Modifier.size(16.dp), tint = DarkPalette.Secondary); Spacer(Modifier.width(4.dp)); Text("$announcementCount avisos", style = MaterialTheme.typography.labelSmall) }
                    }
                }
                // Botones de acción
                IconButton(onClick = onToggleTheme) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema", tint = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant) }
                IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error) }
            }
        }

        // CUADRÍCULA DE MENÚ
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems.size) { index ->
                val item = menuItems[index]
                DirectorCard(item = item, onClick = { onNavigate(item.id) })
            }
        }
    }
}

@Composable
private fun DirectorCard(item: DirectorMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Brush.diagonalGradient(listOf(item.color1, item.color2))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                // Icono con badge
                Box {
                    Icon(item.icon, item.title, modifier = Modifier.size(44.dp), tint = androidx.compose.ui.graphics.Color.White)
                    if (item.badge.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-4).dp),
                            shape = CircleShape,
                            color = DarkPalette.Error
                        ) {
                            Text(item.badge, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(item.description, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            }
        }
    }
}

// Extensión para gradiente diagonal
fun Brush.Companion.diagonalGradient(colors: List<androidx.compose.ui.graphics.Color>): Brush {
    return Brush.linearGradient(colors)
}
