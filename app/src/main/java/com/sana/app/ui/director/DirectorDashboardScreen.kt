package com.sana.app.ui.director

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.repository.UserRecord
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
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("main") }
    var teachers by remember { mutableStateOf(dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }) }
    var announcements by remember { mutableStateOf(listOf<Announcement>()) }
    var directorName by remember { mutableStateOf("Director") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "announcements" -> DirectorAnnouncementsScreen(
                announcements = announcements,
                onAddAnnouncement = { title, content, priority ->
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    announcements = announcements + Announcement(title, content, date, priority)
                },
                onDeleteAnnouncement = { index -> announcements = announcements.toMutableList().also { it.removeAt(index) } },
                isDark = isDark, onBack = { currentScreen = "main" }
            )
            
            "register_teacher" -> DirectorTeacherManagerScreen(
                teachers = teachers, dataRepository = dataRepository, isDark = isDark,
                onBack = { currentScreen = "main" },
                onRefresh = { teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } },
                onTeacherRegistered = { teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }
            )
            
            "view_teachers" -> DirectorTeacherManagerScreen(
                teachers = teachers, dataRepository = dataRepository, isDark = isDark,
                onBack = { currentScreen = "main" },
                onRefresh = { teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } },
                onTeacherRegistered = { }
            )
            
            "reports" -> DirectorReportsScreen(
                teacherCount = teachers.size, announcementCount = announcements.size,
                isDark = isDark, onBack = { currentScreen = "main" }
            )
            
            "logbook" -> {
                // Pantalla simple de bitácora
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📋 Bitácora", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bitácora - Próximamente", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) }
                }
            }
            
            "school_data" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🏫 Mi Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Datos de la escuela - Próximamente", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) }
                }
            }
            
            else -> DirectorMainScreen(
                teacherCount = teachers.size,
                announcementCount = announcements.size,
                isDark = isDark,
                onNavigate = { currentScreen = it },
                onLogout = onNavigateBack,
                onToggleTheme = { scope.launch { themeManager.toggleTheme() } },
                directorName = directorName
            )
        }
    }
}
