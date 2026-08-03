package com.sana.app.ui.teacher

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
fun TeacherDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    
    // Bitácora
    var logEntries by remember { mutableStateOf(listOf<String>()) }
    var newLogEntry by remember { mutableStateOf("") }
    
    // Planes
    var plans by remember { mutableStateOf(listOf<String>()) }
    var newPlan by remember { mutableStateOf("") }
    
    // Guías
    var guides by remember { mutableStateOf(listOf<String>()) }
    var newGuide by remember { mutableStateOf("") }
    
    // Primaria - padres
    var parentName by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var parents by remember { mutableStateOf(dataRepository.getAllUsers().filter { it.role == Constants.ROLE_PARENT }) }
    var generatedParentCode by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "logbook" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📋 Bitácora Escolar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = newLogEntry, onValueChange = { newLogEntry = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva observación") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (newLogEntry.isNotBlank()) { logEntries = logEntries + newLogEntry; newLogEntry = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Guardar") }
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(logEntries) { entry ->
                            Card(shape = RoundedCornerShape(12.dp)) { Text(entry, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
            
            "plans" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎯 Planes de Estudio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = newPlan, onValueChange = { newPlan = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nuevo plan") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (newPlan.isNotBlank()) { plans = plans + newPlan; newPlan = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Crear Plan") }
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(plans) { plan ->
                            Card(shape = RoundedCornerShape(12.dp)) { Text(plan, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
            
            "guides" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📤 Publicar Guías", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = newGuide, onValueChange = { newGuide = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva guía") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (newGuide.isNotBlank()) { guides = guides + newGuide; newGuide = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Publicar Guía") }
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(guides) { guide ->
                            Card(shape = RoundedCornerShape(12.dp)) { Text(guide, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
            
            "primary" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🧒 Primaria - Registrar Padre", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = parentName, onValueChange = { parentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del padre/madre") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = studentName, onValueChange = { studentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del alumno") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (parentName.isNotBlank() && studentName.isNotBlank()) {
                            val code = dataRepository.generateCode("PAD")
                            dataRepository.saveUser(UserRecord(code = code, role = Constants.ROLE_PARENT, name = parentName))
                            generatedParentCode = code
                            parents = dataRepository.getAllUsers().filter { it.role == Constants.ROLE_PARENT }
                            parentName = ""; studentName = ""
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Registrar Padre y Generar Código") }
                    
                    if (generatedParentCode.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✅ Código para el padre:", fontWeight = FontWeight.Bold)
                                Text(generatedParentCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Text("Padres registrados: ${parents.size}", fontWeight = FontWeight.Bold)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(parents) { p ->
                            Card(shape = RoundedCornerShape(8.dp)) { Text("👪 ${p.name} - ${p.code}", modifier = Modifier.padding(12.dp)) }
                        }
                    }
                }
            }
            
            "messages" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📬 Buzón", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                            Spacer(Modifier.height(16.dp))
                            Text("No hay mensajes nuevos", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("👨‍🏫 Panel Docente", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { CardService("📋 Bitácora", Icons.Default.EditNote, "Registro de observaciones", DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "logbook" } }
                        item { CardService("🎯 Planes", Icons.Default.Plumbing, "Crear planes de estudio", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { currentScreen = "plans" } }
                        item { CardService("📤 Guías", Icons.Default.UploadFile, "Publicar material", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { currentScreen = "guides" } }
                        item { CardService("🧒 Primaria", Icons.Default.Toys, "Registrar padres", DarkPalette.Info, DarkPalette.InfoContainer) { currentScreen = "primary" } }
                        item { CardService("📬 Buzón", Icons.Default.Mail, "Mensajes del director", DarkPalette.MoodCalm, DarkPalette.Success) { currentScreen = "messages" } }
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
