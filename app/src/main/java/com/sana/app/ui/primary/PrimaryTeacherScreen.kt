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
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Panel de Docente de Primaria
 * 
 * Panel especial para docentes de nivel primaria:
 * - Crear grupos con padres
 * - Publicar anuncios
 * - Muro de tareas
 * - Economía de fichas
 * - Actividades psicológicas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTeacherScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    themeManager: ThemeManager = hiltViewModel()
) {
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    Box(modifier = Modifier.fillMaxSize()) {
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
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 60)
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
                title = { Text("🧒 Panel Primaria", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getPrimaryTeacherServices()) { service ->
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

data class PrimaryTeacherService(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val gradientColors: List<androidx.compose.ui.graphics.Color>,
    val onClick: () -> Unit
)

@Composable
private fun getPrimaryTeacherServices(): List<PrimaryTeacherService> {
    return listOf(
        PrimaryTeacherService(
            title = "Grupos",
            icon = Icons.Default.GroupWork,
            description = "Crear grupos con padres",
            gradientColors = listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant),
            onClick = { }
        ),
        PrimaryTeacherService(
            title = "Anuncios",
            icon = Icons.Default.Campaign,
            description = "Publicar anuncios",
            gradientColors = listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant),
            onClick = { }
        ),
        PrimaryTeacherService(
            title = "Tareas",
            icon = Icons.Default.Assignment,
            description = "Muro de tareas",
            gradientColors = listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer),
            onClick = { }
        ),
        PrimaryTeacherService(
            title = "Fichas",
            icon = Icons.Default.Stars,
            description = "Economía de fichas",
            gradientColors = listOf(DarkPalette.MoodHappy, DarkPalette.Warning),
            onClick = { }
        ),
        PrimaryTeacherService(
            title = "Actividades",
            icon = Icons.Default.SelfImprovement,
            description = "Actividades psicológicas",
            gradientColors = listOf(DarkPalette.Info, DarkPalette.InfoContainer),
            onClick = { }
        ),
        PrimaryTeacherService(
            title = "Padres",
            icon = Icons.Default.FamilyRestroom,
            description = "Gestionar padres",
            gradientColors = listOf(DarkPalette.MoodCalm, DarkPalette.Success),
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