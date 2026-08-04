package com.sana.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.sana.app.core.repository.FirebaseRepository
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
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var message by remember { mutableStateOf("") }
    var showRegister by remember { mutableStateOf(false) }
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var teacherCount by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }
    var showSchools by remember { mutableStateOf(false) }
    var showCodes by remember { mutableStateOf(false) }
    var schools by remember { mutableStateOf(repo.getAllSchools()) }
    var users by remember { mutableStateOf(repo.getAllUsers()) }
    var isSyncing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when {
            showRegister -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { showRegister = false }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Registrar Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la escuela") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = directorName, onValueChange = { directorName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del director") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = teacherCount, onValueChange = { teacherCount = it.filter { c -> c.isDigit() } }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de docentes") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (schoolName.isNotBlank() && directorName.isNotBlank()) {
                            val sc = repo.generateCode("ESC"); val ac = repo.generateCode("ADM")
                            val count = teacherCount.toIntOrNull() ?: 0
                            val tcs = mutableListOf<String>()
                            for (i in 1..count) { val tc = repo.generateCode("DOC"); tcs.add(tc); repo.saveUser(UserRecord(code = tc, role = Constants.ROLE_TEACHER, name = "Docente $i", schoolCode = sc)) }
                            repo.saveUser(UserRecord(code = ac, role = Constants.ROLE_DIRECTOR, name = directorName, schoolCode = sc))
                            repo.saveSchool(SchoolRecord(code = sc, name = schoolName, adminCode = ac, directorName = directorName, teacherCount = count, teacherCodes = tcs))
                            generatedCodes = "🏫 ESCUELA: $sc\n👔 DIRECTOR: $ac\n👨‍🏫 DOCENTES: ${tcs.joinToString(", ")}"
                            message = "✅ Escuela guardada en Firebase ☁️"
                            schools = repo.getAllSchools(); users = repo.getAllUsers()
                            schoolName = ""; directorName = ""; teacherCount = ""
                        } else { message = "⚠️ Completa los campos" }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Registrar y Guardar ☁️") }
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Text(message, color = if (message.startsWith("✅")) DarkPalette.Success else DarkPalette.Error) }
                    if (generatedCodes.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp)) { Text(generatedCodes, modifier = Modifier.padding(16.dp)) } }
                }
            }
            showSchools -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { showSchools = false }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Escuelas (${schools.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Button(onClick = { schools = repo.getAllSchools() }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
                    LazyColumn { for (s in schools) { item { Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("🏫 ${s.name} | 📝 ${s.code} | 👔 ${s.adminCode}", modifier = Modifier.padding(16.dp)) } } } }
                }
            }
            showCodes -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { showCodes = false }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Códigos (${users.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Button(onClick = { users = repo.getAllUsers() }, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
                    LazyColumn { for (u in users) { item { Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("🔑 ${u.code} | 👤 ${u.name} | 🎭 ${u.role}", modifier = Modifier.padding(16.dp)) } } } }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    
                    // BOTÓN DE SINCRONIZAR
                    Button(onClick = {
                        scope.launch {
                            isSyncing = true
                            val count = repo.syncAll()
                            schools = repo.getAllSchools()
                            users = repo.getAllUsers()
                            message = "✅ Sincronizados $count registros de la nube"
                            isSyncing = false
                        }
                    }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Info)) {
                        if (isSyncing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = androidx.compose.ui.graphics.Color.White)
                        else Icon(Icons.Default.CloudSync, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (isSyncing) "Sincronizando..." else "Sincronizar desde la nube ☁️")
                    }
                    if (message.isNotEmpty() && !showRegister) { Text(message, modifier = Modifier.padding(horizontal = 16.dp), color = if (message.startsWith("✅")) DarkPalette.Success else DarkPalette.TextMuted) }
                    
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { MenuCard("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva", DarkPalette.Primary, DarkPalette.PrimaryVariant) { showRegister = true } }
                        item { MenuCard("Ver\nEscuelas", Icons.Default.ListAlt, "${schools.size} reg.", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { schools = repo.getAllSchools(); showSchools = true } }
                        item { MenuCard("Códigos", Icons.Default.Key, "${users.size} usuarios", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { users = repo.getAllUsers(); showCodes = true } }
                        item { MenuCard("Juegos", Icons.Default.Games, "Gestionar", DarkPalette.Info, DarkPalette.InfoContainer) { } }
                        item { MenuCard("Moderar", Icons.Default.Gavel, "Contenido", DarkPalette.Warning, DarkPalette.WarningContainer) { } }
                        item { MenuCard("Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCard(title: String, icon: ImageVector, desc: String, c1: androidx.compose.ui.graphics.Color, c2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center); Text(desc, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center) }
        }
    }
}
