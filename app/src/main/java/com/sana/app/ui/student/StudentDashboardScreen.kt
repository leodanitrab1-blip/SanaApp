package com.sana.app.ui.student

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Dashboard de Alumno
 * 
 * Panel principal para estudiantes con acceso a:
 * - Chat IA (asistente emocional)
 * - Ejercicios de respiración
 * - Diario emocional
 * - Planes de estudio
 * - Líneas de emergencia
 * - Buzón de mensajes
 * - Juegos
 * - Biblioteca
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    themeManager: ThemeManager = hiltViewModel()
) {
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

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
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 100)
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
            // Barra superior
            TopAppBar(
                title = {
                    Text(
                        "🌿 Hola, Alumno",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Botón de tema
                    IconButton(onClick = { themeManager.toggleTheme() }) {
                        Icon(
                            if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            "Cambiar tema"
                        )
                    }
                    // Notificaciones
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, "Notificaciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            // Cuadrícula de servicios
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getStudentServices()) { service ->
                    ServiceCard(
                        title = service.title,
                        icon = service.icon,
                        description = service.description,
                        gradientColors = service.gradientColors,
                        onClick = service.onClick
                    )
                }
            }
        }
    }
}

/**
 * Modelo de servicio del dashboard
 */
data class StudentService(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val gradientColors: List<androidx.compose.ui.graphics.Color>,
    val onClick: () -> Unit
)

/**
 * Lista de servicios disponibles para el alumno
 */
@Composable
private fun getStudentServices(): List<StudentService> {
    return listOf(
        StudentService(
            title = "Chat IA",
            icon = Icons.Default.Psychology,
            description = "Asistente emocional",
            gradientColors = listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant),
            onClick = { }
        ),
        StudentService(
            title = "Respiración",
            icon = Icons.Default.Air,
            description = "Ejercicios guiados",
            gradientColors = listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant),
            onClick = { }
        ),
        StudentService(
            title = "Diario",
            icon = Icons.Default.Book,
            description = "Registro emocional",
            gradientColors = listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer),
            onClick = { }
        ),
        StudentService(
            title = "Planes",
            icon = Icons.Default.Assignment,
            description = "Planes de estudio",
            gradientColors = listOf(DarkPalette.Info, DarkPalette.InfoContainer),
            onClick = { }
        ),
        StudentService(
            title = "Emergencia",
            icon = Icons.Default.Sos,
            description = "Líneas de ayuda",
            gradientColors = listOf(DarkPalette.Error, DarkPalette.ErrorContainer),
            onClick = { }
        ),
        StudentService(
            title = "Buzón",
            icon = Icons.Default.Email,
            description = "Mensajes y guías",
            gradientColors = listOf(DarkPalette.MoodCalm, DarkPalette.Success),
            onClick = { }
        ),
        StudentService(
            title = "Juegos",
            icon = Icons.Default.Games,
            description = "Aprende jugando",
            gradientColors = listOf(DarkPalette.MoodHappy, DarkPalette.Warning),
            onClick = { }
        ),
        StudentService(
            title = "Biblioteca",
            icon = Icons.Default.LibraryBooks,
            description = "Guías y material",
            gradientColors = listOf(DarkPalette.MoodAnxious, DarkPalette.Tertiary),
            onClick = { }
        )
    )
}

/**
 * Tarjeta de servicio individual
 */
@Composable
private fun ServiceCard(
    title: String,
    icon: ImageVector,
    description: String,
    gradientColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.diagonalGradient(colors = gradientColors)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}