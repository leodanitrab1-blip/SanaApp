package com.sana.app.ui.teacher

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Dashboard de Docente
 * 
 * Panel principal para docentes con acceso a:
 * - Bitácora escolar (registro de observaciones por alumno)
 * - Compartir bitácoras con otros docentes
 * - Publicar guías de estudio (PDFs e imágenes)
 * - Crear planes de estudio (públicos/privados/compartidos)
 * - Buzón de mensajes del director
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
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
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 80)
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
                        "📋 Panel Docente",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { themeManager.toggleTheme() }) {
                        Icon(
                            if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            "Cambiar tema"
                        )
                    }
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
                items(getTeacherServices()) { service ->
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

data class TeacherService(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val gradientColors: List<androidx.compose.ui.graphics.Color>,
    val onClick: () -> Unit
)

@Composable
private fun getTeacherServices(): List<TeacherService> {
    return listOf(
        TeacherService(
            title = "Bitácora",
            icon = Icons.Default.EditNote,
            description = "Registro de observaciones",
            gradientColors = listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant),
            onClick = { }
        ),
        TeacherService(
            title = "Compartir",
            icon = Icons.Default.Share,
            description = "Compartir con docentes",
            gradientColors = listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant),
            onClick = { }
        ),
        TeacherService(
            title = "Guías",
            icon = Icons.Default.UploadFile,
            description = "Publicar material PDF",
            gradientColors = listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer),
            onClick = { }
        ),
        TeacherService(
            title = "Planes",
            icon = Icons.Default.Plumbing,
            description = "Crear planes de estudio",
            gradientColors = listOf(DarkPalette.Info, DarkPalette.InfoContainer),
            onClick = { }
        ),
        TeacherService(
            title = "Buzón",
            icon = Icons.Default.Mail,
            description = "Mensajes del director",
            gradientColors = listOf(DarkPalette.MoodCalm, DarkPalette.Success),
            onClick = { }
        ),
        TeacherService(
            title = "Alumnos",
            icon = Icons.Default.Group,
            description = "Gestionar alumnos",
            gradientColors = listOf(DarkPalette.MoodHappy, DarkPalette.Warning),
            onClick = { }
        )
    )
}

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
                .background(Brush.diagonalGradient(colors = gradientColors)),
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