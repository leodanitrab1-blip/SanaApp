package com.sana.app.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun TeacherDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("main") }
    var logbookEntries by remember { mutableStateOf(listOf<LogbookEntry>()) }
    var studyPlans by remember { mutableStateOf(listOf<StudyPlan>()) }
    var guides by remember { mutableStateOf(listOf<String>()) }
    var parents by remember { mutableStateOf(repo.getAllUsers().filter { it.role == Constants.ROLE_PARENT }) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "logbook" -> TeacherLogbookScreen(logbookEntries, { n, o, c, m -> logbookEntries = logbookEntries + LogbookEntry(logbookEntries.size, n, o, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), c, m) }, { i -> logbookEntries = logbookEntries.toMutableList().also { it.removeAt(i) } }, isDark) { currentScreen = "main" }
            "plans" -> TeacherPlansScreen(studyPlans, { t, s, d, v -> studyPlans = studyPlans + StudyPlan(studyPlans.size, t, s, d, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()), v) }, { i -> studyPlans = studyPlans.toMutableList().also { it.removeAt(i) } }, isDark) { currentScreen = "main" }
            "guides" -> { var ng by remember { mutableStateOf("") }; Column(modifier = Modifier.fillMaxSize().padding(24.dp)) { Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📤 Guías") }; Spacer(Modifier.height(16.dp)); OutlinedTextField(value = ng, onValueChange = { ng = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva guía") }); Button(onClick = { if (ng.isNotBlank()) { guides = guides + ng; ng = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Publicar") } } }
            "primary" -> TeacherPrimaryScreen(parents, repo, isDark, { currentScreen = "main" }, { parents = repo.getAllUsers().filter { it.role == Constants.ROLE_PARENT } })
            "messages" -> { Column(modifier = Modifier.fillMaxSize().padding(24.dp)) { Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📬 Buzón") }; Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay mensajes", color = DarkPalette.TextMuted) } } }
            "share" -> { Column(modifier = Modifier.fillMaxSize().padding(24.dp)) { Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🔗 Compartir") }; Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Próximamente", color = DarkPalette.TextMuted) } } }
            else -> TeacherMainScreen(logbookEntries.size, studyPlans.size, guides.size, parents.size, 0, isDark, { currentScreen = it }, onNavigateBack, { scope.launch { themeManager.toggleTheme() } })
        }
    }
}
