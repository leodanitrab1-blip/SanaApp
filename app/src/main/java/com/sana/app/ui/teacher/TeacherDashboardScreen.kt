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
import com.sana.app.core.repository.DataRepository
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
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("main") }
    var teacherName by remember { mutableStateOf("Docente") }
    var logbookEntries by remember { mutableStateOf(listOf<LogbookEntry>()) }
    var studyPlans by remember { mutableStateOf(listOf<StudyPlan>()) }
    var guides by remember { mutableStateOf(listOf<String>()) }
    var parents by remember { mutableStateOf(dataRepository.getAllUsers().filter { it.role == Constants.ROLE_PARENT }) }
    var unreadMessages by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "logbook" -> TeacherLogbookScreen(
                entries = logbookEntries,
                onAddEntry = { name, obs, cat, mood ->
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    logbookEntries = logbookEntries + LogbookEntry(logbookEntries.size, name, obs, date, cat, mood)
                },
                onDeleteEntry = { index -> logbookEntries = logbookEntries.toMutableList().also { it.removeAt(index) } },
                isDark = isDark, onBack = { currentScreen = "main" }
            )
            
            "plans" -> TeacherPlansScreen(
                plans = studyPlans,
                onAddPlan = { title, subject, desc, vis ->
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    studyPlans = studyPlans + StudyPlan(studyPlans.size, title, subject, desc, date, vis)
                },
                onDeletePlan = { index -> studyPlans = studyPlans.toMutableList().also { it.removeAt(index) } },
                isDark = isDark, onBack = { currentScreen = "main" }
            )
            
            "guides" -> {
                var newGuide by remember { mutableStateOf("") }
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("📤 Guías", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = newGuide, onValueChange = { newGuide = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva guía") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (newGuide.isNotBlank()) { guides = guides + newGuide; newGuide = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Publicar Guía") }
                    Spacer(Modifier.height(16.dp))
                    guides.forEach { guide -> Card(shape = RoundedCornerShape(12.dp)) { Text(guide, modifier = Modifier.padding(16.dp)) } }
                }
            }
            
            "primary" -> TeacherPrimaryScreen(
                parents = parents, dataRepository = dataRepository, isDark = isDark,
                onBack = { currentScreen = "main" },
                onRefresh = { parents = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_PARENT } }
            )
            
            "messages" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("📬 Buzón", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                            Spacer(Modifier.height(16.dp))
                            Text("No hay mensajes nuevos", style = MaterialTheme.typography.titleMedium)
                            Text("Los avisos del director aparecerán aquí", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                        }
                    }
                }
            }
            
            "share" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("🔗 Compartir", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                        Text("Compartir con otros docentes - Próximamente", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) 
                    }
                }
            }
            
            else -> TeacherMainScreen(
                logbookCount = logbookEntries.size,
                plansCount = studyPlans.size,
                guidesCount = guides.size,
                parentsCount = parents.size,
                unreadMessages = unreadMessages,
                isDark = isDark,
                onNavigate = { currentScreen = it },
                onLogout = onNavigateBack,
                onToggleTheme = { scope.launch { themeManager.toggleTheme() } },
                teacherName = teacherName
            )
        }
    }
}
