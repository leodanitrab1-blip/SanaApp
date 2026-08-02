package com.sana.app.ui.admin

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
fun AdminDashboardScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    themeManager: ThemeManager
) {
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context.applicationContext) }
    
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("main") }
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }
    var schools by remember { mutableStateOf(dataRepository.getAllSchools()) }
    var allCodes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "register_school" -> {
                // PANTALLA: REGISTRAR ESCUELA
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main"; message = ""; generatedCodes = "" }) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                        Text("Registrar Escuela", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la escuela") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = directorName, onValueChange = { directorName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del director") }, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(24.dp))
                    
                    Button(onClick = {
                        if (schoolName.isNotBlank() && directorName.isNotBlank()) {
                            try {
                                val schoolCode = dataRepository.generateCode("ESC")
                                val adminCode = dataRepository.generateCode("ADM")
                                
                                dataRepository.saveSchool(SchoolRecord(code = schoolCode, name = schoolName, adminCode = adminCode, directorName = directorName))
                                dataRepository.saveUser(UserRecord(code = adminCode, role = Constants.ROLE_DIRECTOR, name = directorName, schoolCode = schoolCode))
                                
                                generatedCodes = "🏫 ESCUELA: $schoolCode\n👔 DIRECTOR: $adminCode"
                                message = "✅ Escuela registrada correctamente"
                                schools = dataRepository.getAllSchools()
                                schoolName = ""
                                directorName = ""
                            } catch (e: Exception) {
                                message = "❌ Error: ${e.message}"
                            }
                        } else {
                            message = "⚠️ Completa todos los campos"
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary)
                    ) { Text("Registrar Escuela") }
                    
                    if (message.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(
                            containerColor = when { message.startsWith("✅") -> DarkPalette.SuccessContainer; message.startsWith("❌") -> DarkPalette.ErrorContainer; else -> DarkPalette.WarningContainer }
                        )) { Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) }
                    }
                    
                    if (generatedCodes.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.PrimaryContainer)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🔑 CÓDIGOS GENERADOS:", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text(generatedCodes, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Text("⚠️ Entrega el código ADM al director.", style = MaterialTheme.typography.bodySmall, color = DarkPalette.OnSurfaceVariant)
                            }
                        }
                    }
                }
            }
            
            "view_schools" -> {
                // PANTALLA: VER ESCUELAS
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Escuelas Registradas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    if (schools.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay escuelas registradas.", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(schools) { school ->
                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("🏫 ${school.name}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("📝 Código escuela: ${school.code}", style = MaterialTheme.typography.bodyMedium)
                                        Text("👔 Código director: ${school.adminCode}", style = MaterialTheme.typography.bodyMedium)
                                        Text("👤 Director: ${school.directorName}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            "view_codes" -> {
                // PANTALLA: VER CÓDIGOS
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }
                        Text("Códigos de Acceso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    val users = dataRepository.getAllUsers()
                    if (users.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay códigos registrados.", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(users) { user ->
                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("🔑 ${user.code}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("👤 ${user.name}", style = MaterialTheme.typography.bodyMedium)
                                        Text("🎭 Rol: ${user.role}", style = MaterialTheme.typography.bodySmall)
                                        Text("✅ ${if (user.active) "Activo" else "Inactivo"}", style = MaterialTheme.typography.labelSmall, color = if (user.active) DarkPalette.Success else DarkPalette.Error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            else -> {
                // PANTALLA PRINCIPAL
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                        actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f))
                    )

                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { ServiceCard("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva escuela", DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "register_school" } }
                        item { ServiceCard("Ver\nEscuelas", Icons.Default.ListAlt, "Lista completa", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { schools = dataRepository.getAllSchools(); currentScreen = "view_schools" } }
                        item { ServiceCard("Códigos\nde Acceso", Icons.Default.Key, "Ver todos los códigos", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { currentScreen = "view_codes" } }
                        item { ServiceCard("Gestionar\nJuegos", Icons.Default.Games, "Subir y moderar", DarkPalette.Info, DarkPalette.InfoContainer) { message = "📦 Próximamente" } }
                        item { ServiceCard("Moderar\nContenido", Icons.Default.Gavel, "Bitácoras y guías", DarkPalette.Warning, DarkPalette.WarningContainer) { message = "📦 Próximamente" } }
                        item { ServiceCard("Cerrar\nSesión", Icons.Default.Logout, "Salir del panel", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                    
                    if (message.isNotEmpty() && currentScreen == "main") {
                        Spacer(Modifier.height(8.dp))
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.InfoContainer)) {
                            Text(message, modifier = Modifier.padding(16.dp))
                        }
                        LaunchedEffect(message) { kotlinx.coroutines.delay(2000); message = "" }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(title: String, icon: ImageVector, description: String, color1: androidx.compose.ui.graphics.Color, color2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(color1, color2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            }
        }
    }
}
