package com.sana.app.ui.admin

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
import kotlinx.coroutines.launch

data class AdminService(val title: String, val icon: ImageVector, val description: String, val gradientColors: List<androidx.compose.ui.graphics.Color>, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    val services = listOf(
        AdminService("Registrar", Icons.Default.AddBusiness, "Registrar escuelas", listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant)) { },
        AdminService("Escuelas", Icons.Default.ListAlt, "Ver lista", listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant)) { },
        AdminService("Borrar", Icons.Default.DeleteForever, "Eliminar escuelas", listOf(DarkPalette.Error, DarkPalette.ErrorContainer)) { },
        AdminService("Juegos", Icons.Default.Games, "Gestionar juegos", listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer)) { },
        AdminService("Moderar", Icons.Default.Gavel, "Moderar contenido", listOf(DarkPalette.Warning, DarkPalette.WarningContainer)) { },
        AdminService("Estadísticas", Icons.Default.Analytics, "Ver estadísticas", listOf(DarkPalette.Info, DarkPalette.InfoContainer)) { }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))

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
