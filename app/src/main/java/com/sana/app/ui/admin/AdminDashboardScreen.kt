package com.sana.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("main") }
    var schools by remember { mutableStateOf(repo.getAllSchools()) }
    var allUsers by remember { mutableStateOf(repo.getAllUsers()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register" -> RegisterSchoolScreen(repo, isDark, onBack = { currentScreen = "main" }, onSchoolRegistered = { schools = repo.getAllSchools(); allUsers = repo.getAllUsers() })
            "view_schools" -> ViewSchoolsScreen(schools, repo, isDark, onBack = { currentScreen = "main" }, onRefresh = { schools = repo.getAllSchools() })
            "view_codes" -> ViewCodesScreen(allUsers, repo, isDark, onBack = { currentScreen = "main" }, onRefresh = { allUsers = repo.getAllUsers() })
            "games" -> GamesManagerScreen(loadGames(context), isDark, context, onBack = { currentScreen = "main" }, onGamesUpdated = { })
            "moderate" -> ModerateScreen(isDark, onBack = { currentScreen = "main" })
            else -> AdminMainScreen(
                schoolsCount = schools.size, usersCount = allUsers.size, isDark = isDark,
                onNavigate = { currentScreen = it }, onLogout = onNavigateBack,
                onToggleTheme = { scope.launch { themeManager.toggleTheme() } }
            )
        }
    }
}
