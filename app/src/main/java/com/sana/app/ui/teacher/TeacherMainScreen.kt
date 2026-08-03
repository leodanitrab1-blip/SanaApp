package com.sana.app.ui.teacher

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

data class TeacherMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val color1: androidx.compose.ui.graphics.Color,
    val color2: androidx.compose.ui.graphics.Color,
    val badge: String = "",
    val emoji: String = ""
)

@Composable
fun TeacherMainScreen(
    logbookCount: Int,
    plansCount: Int,
    guidesCount: Int,
    parentsCount: Int,
    unreadMessages: Int,
    isDark: Boolean,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
    teacherName: String = "Docente"
) {
    // Animación de pulso para el botón de mensajes
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    val menuItems = listOf(
        TeacherMenuItem("logbook", "Bitácora", Icons.Default.EditNote, "Registro de observaciones", DarkPalette.Primary, DarkPalette.PrimaryVariant, "$logbookCount", "📋"),
        TeacherMenuItem("plans", "Planes", Icons.Default.Plumbing, "Planes de estudio", DarkPalette.Secondary, DarkPalette.SecondaryVariant, "$plansCount", "🎯"),
        TeacherMenuItem("guides", "Guías", Icons.Default.UploadFile, "Material didáctico", DarkPalette.Tertiary, DarkPalette.TertiaryContainer, "$guidesCount", "📤"),
        TeacherMenuItem("primary", "Primaria", Icons.Default.Toys, "Grupos y padres", DarkPalette.Info, DarkPalette.InfoContainer, "$parentsCount", "🧒"),
        TeacherMenuItem("messages", "Buzón", Icons.Default.Mail, "Mensajes del director", DarkPalette.MoodCalm, DarkPalette.Success, if (unreadMessages > 0) "$unreadMessages" else "", "📬"),
        TeacherMenuItem("share", "Compartir", Icons.Default.Share, "Con otros docentes", DarkPalette.MoodHappy, DarkPalette.Warning, "", "🔗")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // CABECERA ELEGANTE CON ESTADÍSTICAS
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) 
                                else DarkPalette.Primary.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar animado
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        color = DarkPalette.Primary,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                teacherName.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "👨‍🏫 $teacherName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
                        )
                        Text(
                            "Panel Docente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                        )
                    }
                    // Botones de acción
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            "Tema",
                            tint = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error)
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // BARRA DE ESTADÍSTICAS RÁPIDAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBadge("Bitácora", logbookCount, DarkPalette.Primary)
                    StatBadge("Planes", plansCount, DarkPalette.Secondary)
                    StatBadge("Guías", guidesCount, DarkPalette.Tertiary)
                    StatBadge("Padres", parentsCount, DarkPalette.Info)
                }
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
                TeacherCard(
                    item = item,
                    onClick = { onNavigate(item.id) },
                    pulseScale = if (item.id == "messages" && unreadMessages > 0) pulseScale else 1f
                )
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.2f)
        ) {
            Text(
                "$count",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
    }
}

@Composable
private fun TeacherCard(item: TeacherMenuItem, onClick: () -> Unit, pulseScale: Float = 1f) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(pulseScale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(item.color1, item.color2))),
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
                            Text(
                                item.badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
