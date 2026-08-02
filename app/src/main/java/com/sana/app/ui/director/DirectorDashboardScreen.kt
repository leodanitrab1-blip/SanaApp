package com.sana.app.ui.director

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }
    var teachers by remember { mutableStateOf(dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }) }
    var announcements by remember { mutableStateOf(listOf<String>()) }
    var newAnnouncement by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register_teacher" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Registrar Docente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del docente") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (teacherName.isNotBlank()) {
                            val code = dataRepository.generateCode("DOC")
                            dataRepository.saveUser(UserRecord(code = code, role = Constants.ROLE_TEACHER, name = teacherName))
                            generatedCode = code
                            message = "✅ Docente registrado"
                            teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }
                            teacherName = ""
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Generar Código") }
                    
                    if (generatedCode.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✅ Código generado:", fontWeight = FontWeight.Bold)
                                Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            "announcements" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Avisos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = newAnnouncement, onValueChange = { newAnnouncement = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nuevo aviso") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (newAnnouncement.isNotBlank()) {
                            announcements = announcements + newAnnouncement
                            newAnnouncement = ""
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Publicar") }
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(announcements) { a ->
                            Card(shape = RoundedCornerShape(12.dp)) { Text(a, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
            
            "view_teachers" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Docentes (${teachers.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(teachers) { t ->
                            Card(shape = RoundedCornerShape(16.dp)) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(t.name, fontWeight = FontWeight.Bold)
                                        Text("🔑 ${t.code}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { dataRepository.deactivateUser(t.code); teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }) {
                                        Icon(Icons.Default.Delete, "Dar de baja", tint = DarkPalette.Error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("👔 Panel Director", fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                        actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f))
                    )
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { CardService("📢 Avisos", Icons.Default.Campaign, "Publicar", DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "announcements" } }
                        item { CardService("➕ Docente", Icons.Default.PersonAdd, "Registrar", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { currentScreen = "register_teacher" } }
                        item { CardService("👨‍🏫 Docentes", Icons.Default.Group, "${teachers.size} activos", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { teachers = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }; currentScreen = "view_teachers" } }
                        item { CardService("📊 Reportes", Icons.Default.Assessment, "Ver reportes", DarkPalette.Info, DarkPalette.InfoContainer) { message = "📊 En desarrollo" } }
                        item { CardService("📋 Bitácora", Icons.Default.HistoryEdu, "Mi bitácora", DarkPalette.MoodCalm, DarkPalette.Success) { message = "📋 En desarrollo" } }
                        item { CardService("🚪 Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardService(title: String, icon: ImageVector, desc: String, color1: androidx.compose.ui.graphics.Color, color2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
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
