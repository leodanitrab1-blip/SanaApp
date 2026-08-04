package com.sana.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import com.sana.app.ui.student.chat.ChatScreen
import com.sana.app.ui.student.diary.EmergencyScreen
import com.sana.app.ui.games.GamesScreen
import com.sana.app.ui.library.LibraryScreen
import kotlinx.coroutines.launch

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
            "chat" -> ChatScreen(userId = userId, onNavigateBack = { currentScreen = "main" }, themeManager = themeManager)
            "breathing" -> StudentBreathingScreen(isDark = isDark, onBack = { currentScreen = "main" })
            "diary" -> StudentDiaryScreen(isDark = isDark, onBack = { currentScreen = "main" })
            "emergency" -> EmergencyScreen(onNavigateBack = { currentScreen = "main" }, themeManager = themeManager)
            "games" -> GamesScreen(onNavigateBack = { currentScreen = "main" }, onGameSelected = {}, themeManager = themeManager)
            "library" -> LibraryScreen(onNavigateBack = { currentScreen = "main" }, themeManager = themeManager)
            "plans" -> PlaceholderScreen("📚 Planes", "Próximamente", isDark) { currentScreen = "main" }
            "inbox" -> PlaceholderScreen("📬 Buzón", "Próximamente", isDark) { currentScreen = "main" }
            else -> StudentMainScreen(diaryCount = 0, breathingSessions = 0, isDark = isDark, onNavigate = { currentScreen = it }, onLogout = onNavigateBack, onToggleTheme = { scope.launch { themeManager.toggleTheme() } })
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, desc: String, isDark: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(desc, style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) }
    }
}
