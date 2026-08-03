package com.sana.app.ui.admin

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("main") }
    var schools by remember { mutableStateOf(dataRepository.getAllSchools()) }
    var allUsers by remember { mutableStateOf(dataRepository.getAllUsers()) }
    var savedGames by remember { mutableStateOf(loadGames(context)) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register_school" -> RegisterSchoolScreen(dataRepository, isDark, onBack = { currentScreen = "main" }, onSchoolRegistered = { schools = dataRepository.getAllSchools(); allUsers = dataRepository.getAllUsers() })
            "view_schools" -> ViewSchoolsScreen(schools, dataRepository, isDark, onBack = { currentScreen = "main" }, onRefresh = { schools = dataRepository.getAllSchools() })
            "view_codes" -> ViewCodesScreen(allUsers, dataRepository, isDark, onBack = { currentScreen = "main" }, onRefresh = { allUsers = dataRepository.getAllUsers() })
            "games_manager" -> GamesManagerScreen(savedGames, isDark, context, onBack = { currentScreen = "main" }, onGamesUpdated = { savedGames = loadGames(context) })
            "moderate" -> ModerateScreen(isDark, onBack = { currentScreen = "main" })
            else -> AdminMainScreen(schools.size, allUsers.size, isDark, onNavigate = { currentScreen = it }, onLogout = onNavigateBack, onToggleTheme = { scope.launch { themeManager.toggleTheme() } })
        }
    }
}
