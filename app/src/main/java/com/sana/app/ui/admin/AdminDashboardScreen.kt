package com.sana.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.repository.SchoolRecord
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("main") }
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var teacherCount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }
    var schools by remember { mutableStateOf(dataRepository.getAllSchools()) }
    var allUsers by remember { mutableStateOf(dataRepository.getAllUsers()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register_school" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main"; message = ""; generatedCodes = "" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Registrar Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la escuela") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = directorName, onValueChange = { directorName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del director") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = teacherCount, onValueChange = { teacherCount = it.filter { c -> c.isDigit() } }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de docentes") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(24.dp))
                    
                    Button(onClick = {
                        if (schoolName.isNotBlank() && directorName.isNotBlank()) {
                            try {
                                val schoolCode = dataRepository.generateCode("ESC")
                                val adminCode = dataRepository.generateCode("ADM")
                                val count = teacherCount.toIntOrNull() ?: 0
                                val teacherCodes = mutableListOf<String>()
                                for (i in 1..count) {
                                    val tCode = dataRepository.generateCode("DOC")
                                    teacherCodes.add(tCode)
                                    dataRepository.saveUser(UserRecord(code = tCode, role = Constants.ROLE_TEACHER, name = "Docente $i", schoolCode = schoolCode))
                                }
                                dataRepository.saveSchool(SchoolRecord(code = schoolCode, name = schoolName, adminCode = adminCode, directorName = directorName, teacherCount = count, teacherCodes = teacherCodes))
                                dataRepository.saveUser(UserRecord(code = adminCode, role = Constants.ROLE_DIRECTOR, name = directorName, schoolCode = schoolCode))
                                
                                val sb = StringBuilder()
                                sb.appendLine("🏫 ESCUELA: $schoolCode")
                                sb.appendLine("👔 DIRECTOR: $adminCode")
                                if (teacherCodes.isNotEmpty()) { sb.appendLine("\n👨‍🏫 DOCENTES:"); teacherCodes.forEachIndexed { i, c -> sb.appendLine("  ${i+1}. $c") } }
                                generatedCodes = sb.toString()
                                message = "✅ Escuela registrada"
                                schools = dataRepository.getAllSchools()
                                allUsers = dataRepository.getAllUsers()
                                schoolName = ""; directorName = ""; teacherCount = ""
                            } catch (e: Exception) { message = "❌ Error: ${e.message}" }
                        } else { message = "⚠️ Completa los campos" }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Registrar y Generar Códigos") }
                    
                    if (message.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (message.startsWith("✅")) DarkPalette.SuccessContainer else DarkPalette.ErrorContainer)) { Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) } }
                    if (generatedCodes.isNotEmpty()) { Spacer(Modifier.height(12.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.PrimaryContainer)) { Column(modifier = Modifier.padding(16.dp)) { Text("🔑 CÓDIGOS:", fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text(generatedCodes, style = MaterialTheme.typography.bodyMedium) } } }
                }
            }
            
            "view_schools" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Escuelas (${schools.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { schools = dataRepository.getAllSchools() }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar lista") }
                    Spacer(Modifier.height(8.dp))
                    if (schools.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay escuelas registradas.", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) } }
                    else { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(schools) { s -> Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) { Column(modifier = Modifier.padding(16.dp)) { Text("🏫 ${s.name}", fontWeight = FontWeight.Bold); Text("📝 Escuela: ${s.code}"); Text("👔 Director: ${s.adminCode}"); Text("👨‍🏫 Docentes: ${s.teacherCount}") } } } } }
                }
            }
            
            "view_codes" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Códigos (${allUsers.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { allUsers = dataRepository.getAllUsers() }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
                    Spacer(Modifier.height(8.dp))
                    if (allUsers.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay códigos.", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) } }
                    else { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(allUsers) { u -> Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(modifier = Modifier.weight(1f)) { Text("🔑 ${u.code}", fontWeight = FontWeight.Bold); Text("👤 ${u.name} (${u.role})") }; Icon(if (u.active) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = if (u.active) DarkPalette.Success else DarkPalette.Error) } } } } }
                }
            }
            
            "games_manager" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Gestionar Juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Games, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                            Spacer(Modifier.height(16.dp))
                            Text("Gestión de juegos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Próximamente podrás subir juegos HTML", style = MaterialTheme.typography.bodyMedium, color = DarkPalette.TextMuted)
                        }
                    }
                }
            }
            
            "moderate" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Moderar Contenido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Gavel, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                            Spacer(Modifier.height(16.dp))
                            Text("Moderación de contenido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Próximamente podrás moderar bitácoras y guías", style = MaterialTheme.typography.bodyMedium, color = DarkPalette.TextMuted)
                        }
                    }
                }
            }
            
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { CardService("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva", DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "register_school" } }
                        item { CardService("Ver\nEscuelas", Icons.Default.ListAlt, "${schools.size} registradas", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { schools = dataRepository.getAllSchools(); currentScreen = "view_schools" } }
                        item { CardService("Códigos", Icons.Default.Key, "${allUsers.size} usuarios", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { allUsers = dataRepository.getAllUsers(); currentScreen = "view_codes" } }
                        item { CardService("Gestionar\nJuegos", Icons.Default.Games, "Subir y moderar", DarkPalette.Info, DarkPalette.InfoContainer) { currentScreen = "games_manager" } }
                        item { CardService("Moderar\nContenido", Icons.Default.Gavel, "Bitácoras, guías", DarkPalette.Warning, DarkPalette.WarningContainer) { currentScreen = "moderate" } }
                        item { CardService("Cerrar\nSesión", Icons.Default.Logout, "Salir", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
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
