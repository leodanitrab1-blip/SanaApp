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
import com.sana.app.ui.student.chat.ChatScreen
import com.sana.app.ui.student.diary.DiaryScreen
import com.sana.app.ui.student.diary.EmergencyScreen
import com.sana.app.ui.games.GamesScreen
import com.sana.app.ui.library.LibraryScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("main") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 100) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "chat" -> { ChatScreen(userId = userId, onNavigateBack = { currentScreen = "main" }, themeManager = themeManager) }
            "breathing" -> { StudentBreathingScreen(isDark = isDark, onBack = { currentScreen = "main" }) }
            "diary" -> { StudentDiaryScreen(isDark = isDark, onBack = { currentScreen = "main" }) }
            "emergency" -> { EmergencyScreen(onNavigateBack = { currentScreen = "main" }, themeManager = themeManager) }
            "games" -> { GamesScreen(onNavigateBack = { currentScreen = "main" }, onGameSelected = {}, themeManager = themeManager) }
            "library" -> { LibraryScreen(onNavigateBack = { currentScreen = "main" }, themeManager = themeManager) }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🎓 Alumno") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { StudentCard("🤖 Chat IA", Icons.Default.Psychology, DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "chat" } }
                        item { StudentCard("🫁 Respiración", Icons.Default.Air, DarkPalette.Secondary, DarkPalette.SecondaryVariant) { currentScreen = "breathing" } }
                        item { StudentCard("📝 Diario", Icons.Default.Book, DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { currentScreen = "diary" } }
                        item { StudentCard("🆘 Ayuda", Icons.Default.Sos, DarkPalette.Error, DarkPalette.ErrorContainer) { currentScreen = "emergency" } }
                        item { StudentCard("🎮 Juegos", Icons.Default.Games, DarkPalette.MoodHappy, DarkPalette.Warning) { currentScreen = "games" } }
                        item { StudentCard("📖 Biblioteca", Icons.Default.LibraryBooks, DarkPalette.Info, DarkPalette.InfoContainer) { currentScreen = "library" } }
                        item { StudentCard("📬 Buzón", Icons.Default.Email, DarkPalette.MoodCalm, DarkPalette.Success) { } }
                        item { StudentCard("🚪 Salir", Icons.Default.Logout, DarkPalette.Warning, DarkPalette.Error) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentCard(title: String, icon: ImageVector, c1: androidx.compose.ui.graphics.Color, c2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center) }
        }
    }
}
