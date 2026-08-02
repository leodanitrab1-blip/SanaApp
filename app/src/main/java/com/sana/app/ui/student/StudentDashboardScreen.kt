package com.sana.app.ui.student

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
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import com.sana.app.ui.student.breathing.BreathingScreen
import com.sana.app.ui.student.chat.ChatScreen
import com.sana.app.ui.student.diary.DiaryScreen
import com.sana.app.ui.student.diary.EmergencyScreen
import com.sana.app.ui.games.GamesScreen
import com.sana.app.ui.library.LibraryScreen
import kotlinx.coroutines.launch

// Pantallas disponibles para el alumno
enum class StudentScreen {
    DASHBOARD, CHAT, BREATHING, DIARY, PLANS, EMERGENCY, INBOX, GAMES, LIBRARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(StudentScreen.DASHBOARD) }

    when (currentScreen) {
        StudentScreen.CHAT -> {
            ChatScreen(userId = userId, onNavigateBack = { currentScreen = StudentScreen.DASHBOARD }, themeManager = themeManager)
        }
        StudentScreen.BREATHING -> {
            BreathingScreen(userId = userId, onNavigateBack = { currentScreen = StudentScreen.DASHBOARD }, themeManager = themeManager)
        }
        StudentScreen.DIARY -> {
            DiaryScreen(userId = userId, onNavigateBack = { currentScreen = StudentScreen.DASHBOARD }, themeManager = themeManager)
        }
        StudentScreen.EMERGENCY -> {
            EmergencyScreen(onNavigateBack = { currentScreen = StudentScreen.DASHBOARD }, themeManager = themeManager)
        }
        StudentScreen.GAMES -> {
            GamesScreen(
                onNavigateBack = { currentScreen = StudentScreen.DASHBOARD },
                onGameSelected = { },
                themeManager = themeManager
            )
        }
        StudentScreen.LIBRARY -> {
            LibraryScreen(onNavigateBack = { currentScreen = StudentScreen.DASHBOARD }, themeManager = themeManager)
        }
        else -> {
            // DASHBOARD PRINCIPAL
            Box(modifier = Modifier.fillMaxSize()) {
                if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 100) }
                else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("🎓 Panel Alumno", fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                        actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f))
                    )

                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { ServiceCard("🤖 Chat IA", Icons.Default.Psychology, "Asistente emocional", DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = StudentScreen.CHAT } }
                        item { ServiceCard("🫁 Respiración", Icons.Default.Air, "Ejercicios guiados", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { currentScreen = StudentScreen.BREATHING } }
                        item { ServiceCard("📝 Diario", Icons.Default.Book, "Registro emocional", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { currentScreen = StudentScreen.DIARY } }
                        item { ServiceCard("📚 Planes", Icons.Default.Assignment, "Planes de estudio", DarkPalette.Info, DarkPalette.InfoContainer) { currentScreen = StudentScreen.PLANS } }
                        item { ServiceCard("🆘 Ayuda", Icons.Default.Sos, "Líneas de emergencia", DarkPalette.Error, DarkPalette.ErrorContainer) { currentScreen = StudentScreen.EMERGENCY } }
                        item { ServiceCard("📬 Buzón", Icons.Default.Email, "Mensajes", DarkPalette.MoodCalm, DarkPalette.Success) { currentScreen = StudentScreen.INBOX } }
                        item { ServiceCard("🎮 Juegos", Icons.Default.Games, "Aprende jugando", DarkPalette.MoodHappy, DarkPalette.Warning) { currentScreen = StudentScreen.GAMES } }
                        item { ServiceCard("📖 Biblioteca", Icons.Default.LibraryBooks, "Guías y material", DarkPalette.MoodAnxious, DarkPalette.Tertiary) { currentScreen = StudentScreen.LIBRARY } }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(title: String, icon: ImageVector, desc: String, color1: androidx.compose.ui.graphics.Color, color2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(color1, color2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            }
        }
    }
}
