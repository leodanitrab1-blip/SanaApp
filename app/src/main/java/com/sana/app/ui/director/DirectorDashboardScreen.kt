package com.sana.app.ui.director

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
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DirectorDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("main") }
    var teachers by remember { mutableStateOf(repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }) }
    var announcements by remember { mutableStateOf(listOf<Announcement>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "announcements" -> DirectorAnnouncementsScreen(announcements, { t, c, p -> announcements = announcements + Announcement(t, c, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), p) }, { i -> announcements = announcements.toMutableList().also { it.removeAt(i) } }, isDark) { currentScreen = "main" }
            "register_teacher" -> DirectorTeacherManagerScreen(teachers, repo, isDark, { currentScreen = "main" }, { teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }, { teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } })
            "view_teachers" -> DirectorTeacherManagerScreen(teachers, repo, isDark, { currentScreen = "main" }, { teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }, { })
            "reports" -> DirectorReportsScreen(teachers.size, announcements.size, isDark) { currentScreen = "main" }
            "logbook" -> { /* placeholder */ }
            "school_data" -> { /* placeholder */ }
            else -> DirectorMainScreen(teachers.size, announcements.size, isDark, { currentScreen = it }, onNavigateBack, { scope.launch { themeManager.toggleTheme() } })
        }
    }
}
