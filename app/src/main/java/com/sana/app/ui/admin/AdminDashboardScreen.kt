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

    var showRegisterSchool by remember { mutableStateOf(false) }
    var showSchoolList by remember { mutableStateOf(false) }
    var schoolName by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var generatedCodes by remember { mutableStateOf("") }
    var schools by remember { mutableStateOf(dataRepository.getAllSchools()) }

    if (showRegisterSchool) {
        // REGISTRAR ESCUELA
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showRegisterSchool = false; message = ""; generatedCodes = "" }) {
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
                        
                        // Guardar escuela
                        dataRepository.saveSchool(SchoolRecord(
                            code = schoolCode,
                            name = schoolName,
                            adminCode = adminCode,
                            directorName = directorName
                        ))
                        
                        // Guardar director como usuario
                        dataRepository.saveUser(UserRecord(
                            code = adminCode,
                            role = Constants.ROLE_DIRECTOR,
                            name = directorName,
                            schoolCode = schoolCode
                        ))
                        
                        generatedCodes = "🏫 Escuela: $schoolCode\n👔 Director: $adminCode"
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
                    containerColor = when {
                        message.startsWith("✅") -> DarkPalette.SuccessContainer
                        message.startsWith("❌") -> DarkPalette.ErrorContainer
                        else -> DarkPalette.WarningContainer
                    }
                )) {
                    Text(message, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                }
            }
            
            if (generatedCodes.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.PrimaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🔑 CÓDIGOS GENERADOS:", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(generatedCodes, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("⚠️ Entrega el código ADM al director para que pueda acceder.", style = MaterialTheme.typography.bodySmall, color = DarkPalette.OnSurfaceVariant)
                    }
                }
            }
        }
    } else if (showSchoolList) {
        // LISTA DE ESCUELAS
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showSchoolList = false }) { Icon(Icons.Default.ArrowBack, "Volver") }
                Text("Escuelas Registradas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            
            if (schools.isEmpty()) {
                Text("No hay escuelas registradas aún.", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(schools) { school ->
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🏫 ${school.name}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("📝 Código: ${school.code}", style = MaterialTheme.typography.bodyMedium)
                                Text("👔 Admin: ${school.adminCode}", style = MaterialTheme.typography.bodyMedium)
                                Text("👤 Director: ${school.directorName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // PANTALLA PRINCIPAL ADMIN
        Box(modifier = Modifier.fillMaxSize()) {
            if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
            else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("⚙️ Administración", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                    actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f))
                )

                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { ServiceCard("Registrar\nEscuela", Icons.Default.AddBusiness, "Crear nueva", listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant)) { showRegisterSchool = true } }
                    item { ServiceCard("Ver\nEscuelas", Icons.Default.ListAlt, "Lista completa", listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant)) { schools = dataRepository.getAllSchools(); showSchoolList = true } }
                    item { ServiceCard("Códigos", Icons.Default.Key, "Ver todos", listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer)) {
                        val users = dataRepository.getAllUsers()
                        generatedCodes = users.joinToString("\n") { "${it.code} → ${it.name} (${it.role})" }
                        message = "✅ ${users.size} usuarios registrados"
                    } }
                    item { ServiceCard("Juegos", Icons.Default.Games, "Gestionar", listOf(DarkPalette.Info, DarkPalette.InfoContainer)) { } }
                    item { ServiceCard("Moderar", Icons.Default.Gavel, "Contenido", listOf(DarkPalette.Warning, DarkPalette.WarningContainer)) { } }
                    item { ServiceCard("Salir", Icons.Default.Logout, "Cerrar sesión", listOf(DarkPalette.Error, DarkPalette.ErrorContainer)) { onNavigateBack() } }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(title: String, icon: ImageVector, description: String, gradientColors: List<androidx.compose.ui.graphics.Color>, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = gradientColors)), contentAlignment = Alignment.Center) {
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
