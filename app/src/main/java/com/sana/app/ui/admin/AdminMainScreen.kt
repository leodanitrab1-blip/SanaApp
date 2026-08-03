package com.sana.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    schoolsCount: Int,
    usersCount: Int,
    isDark: Boolean,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onLogout) { Icon(Icons.Default.ArrowBack, "Volver") } },
            actions = {
                IconButton(onClick = onToggleTheme) {
                    Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) 
                                else LightPalette.Surface.copy(alpha = 0.9f)
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AdminCard("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva", DarkPalette.Primary, DarkPalette.PrimaryVariant) { onNavigate("register_school") }
            }
            item {
                AdminCard("Ver\nEscuelas", Icons.Default.ListAlt, "$schoolsCount registradas", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { onNavigate("view_schools") }
            }
            item {
                AdminCard("Códigos", Icons.Default.Key, "$usersCount usuarios", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { onNavigate("view_codes") }
            }
            item {
                AdminCard("Gestionar\nJuegos", Icons.Default.Games, "Subir HTML", DarkPalette.Info, DarkPalette.InfoContainer) { onNavigate("games_manager") }
            }
            item {
                AdminCard("Moderar\nContenido", Icons.Default.Gavel, "Bitácoras, guías", DarkPalette.Warning, DarkPalette.WarningContainer) { onNavigate("moderate") }
            }
            item {
                AdminCard("Cerrar\nSesión", Icons.Default.Logout, "Salir", DarkPalette.Error, DarkPalette.ErrorContainer) { onLogout() }
            }
        }
    }
}

@Composable
fun AdminCard(
    title: String, icon: ImageVector, description: String,
    color1: androidx.compose.ui.graphics.Color, color2: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(color1, color2))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            }
        }
    }
}
