package com.sana.app.ui.director

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Announcement(val title: String, val content: String, val date: String, val priority: String = "NORMAL")
data class DirectorLogEntry(val id: Int, val content: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    
    // Datos
    var teachers by remember { mutableStateOf(repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }) }
    var announcements by remember { mutableStateOf(listOf<Announcement>()) }
    var logEntries by remember { mutableStateOf(listOf<DirectorLogEntry>()) }
    var directorName by remember { mutableStateOf("Director") }
    
    // Formularios
    var newAnnouncementTitle by remember { mutableStateOf("") }
    var newAnnouncementContent by remember { mutableStateOf("") }
    var newAnnouncementPriority by remember { mutableStateOf("NORMAL") }
    var teacherName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }
    var logContent by remember { mutableStateOf("") }
    var teacherToDelete by remember { mutableStateOf<UserRecord?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            // ============ AVISOS ============
            "announcements" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📢 Avisos y Comunicados", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    
                    // Formulario nuevo aviso
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nuevo comunicado para docentes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = newAnnouncementTitle, onValueChange = { newAnnouncementTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = newAnnouncementContent, onValueChange = { newAnnouncementContent = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Contenido del aviso") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("ALTA" to "🔴 Alta", "NORMAL" to "🟡 Normal", "BAJA" to "🟢 Baja").forEach { (v, l) ->
                                    FilterChip(selected = newAnnouncementPriority == v, onClick = { newAnnouncementPriority = v }, label = { Text(l) })
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (newAnnouncementTitle.isNotBlank() && newAnnouncementContent.isNotBlank()) {
                                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                    announcements = listOf(Announcement(newAnnouncementTitle, newAnnouncementContent, date, newAnnouncementPriority)) + announcements
                                    message = "✅ Aviso publicado para docentes"
                                    newAnnouncementTitle = ""; newAnnouncementContent = ""; newAnnouncementPriority = "NORMAL"
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) {
                                Icon(Icons.Default.Campaign, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Publicar Aviso 📢")
                            }
                        }
                    }
                    
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Text(message, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
                    
                    Spacer(Modifier.height(16.dp))
                    Text("Avisos publicados (${announcements.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    if (announcements.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay avisos publicados", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = announcements.size
                            for (i in 0 until size) {
                                val a = announcements[i]
                                item(key = "ann_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = when (a.priority) { "ALTA" -> DarkPalette.Error; "BAJA" -> DarkPalette.Success; else -> DarkPalette.Warning }) {}
                                                Spacer(Modifier.width(8.dp))
                                                Text(a.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                                Text(a.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
                                                IconButton(onClick = { announcements = announcements.filterIndexed { index, _ -> index != i } }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Eliminar", tint = DarkPalette.Error, modifier = Modifier.size(18.dp)) }
                                            }
                                            Spacer(Modifier.height(8.dp))
                                            Text(a.content, style = MaterialTheme.typography.bodyMedium)
                                            Spacer(Modifier.height(4.dp))
                                            Surface(shape = RoundedCornerShape(4.dp), color = when (a.priority) { "ALTA" -> DarkPalette.Error.copy(alpha = 0.2f); "BAJA" -> DarkPalette.Success.copy(alpha = 0.2f); else -> DarkPalette.Warning.copy(alpha = 0.2f) }) { Text("Prioridad: " + a.priority, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ REGISTRAR DOCENTE ============
            "register_teacher" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Registrar Docente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(20.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Nuevo docente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre completo del docente") }, leadingIcon = { Icon(Icons.Default.Person, null, tint = DarkPalette.Primary) }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = {
                                if (teacherName.isNotBlank()) {
                                    val code = repo.generateCode("DOC")
                                    repo.saveUser(UserRecord(code = code, role = Constants.ROLE_TEACHER, name = teacherName))
                                    generatedCode = code
                                    message = "✅ Docente registrado"
                                    teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }
                                    teacherName = ""
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) {
                                Icon(Icons.Default.Key, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Generar Código de Acceso")
                            }
                            if (generatedCode.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))
                                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CheckCircle, null, tint = DarkPalette.Success, modifier = Modifier.size(32.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("✅ Código generado", fontWeight = FontWeight.Bold)
                                        Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkPalette.Primary)
                                        Text("Entrega este código al docente", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ VER DOCENTES ============
            "view_teachers" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Docentes (${teachers.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Secondary)) { Text("Actualizar") }
                    Spacer(Modifier.height(12.dp))
                    if (teachers.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay docentes registrados", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = teachers.size
                            for (i in 0 until size) {
                                val t = teachers[i]
                                item(key = "t_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text(t.name.take(1).uppercase(), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) { Text(t.name, fontWeight = FontWeight.Bold); Text(t.code, style = MaterialTheme.typography.bodySmall, color = DarkPalette.Primary) }
                                            Surface(shape = RoundedCornerShape(8.dp), color = if (t.active) DarkPalette.Success.copy(alpha = 0.2f) else DarkPalette.Error.copy(alpha = 0.2f)) { Text(if (t.active) "Activo" else "Inactivo", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (t.active) DarkPalette.Success else DarkPalette.Error) }
                                            IconButton(onClick = { teacherToDelete = t; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Dar de baja", tint = DarkPalette.Error) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ BITÁCORA ============
            "logbook" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📋 Bitácora del Director", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nueva entrada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = logContent, onValueChange = { logContent = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Registro del día") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (logContent.isNotBlank()) {
                                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                    logEntries = listOf(DirectorLogEntry(logEntries.size, logContent, date)) + logEntries
                                    message = "✅ Entrada guardada"
                                    logContent = ""
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Info)) { Text("Guardar en Bitácora") }
                        }
                    }
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Text(message, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
                    Spacer(Modifier.height(16.dp))
                    Text("Entradas (${logEntries.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (logEntries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bitácora vacía", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = logEntries.size
                            for (i in 0 until size) {
                                val entry = logEntries[i]
                                item(key = "log_$i") {
                                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(entry.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
                                            Spacer(Modifier.height(4.dp))
                                            Text(entry.content, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ REPORTES ============
            "reports" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📊 Reportes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(20.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) { Column(modifier = Modifier.padding(20.dp)) { Row { Icon(Icons.Default.Group, null, tint = DarkPalette.Primary, modifier = Modifier.size(32.dp)); Spacer(Modifier.width(12.dp)); Column { Text("Docentes activos", fontWeight = FontWeight.Bold); Text("${teachers.size}", style = MaterialTheme.typography.headlineMedium, color = DarkPalette.Primary) } } } }
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) { Column(modifier = Modifier.padding(20.dp)) { Row { Icon(Icons.Default.Campaign, null, tint = DarkPalette.Secondary, modifier = Modifier.size(32.dp)); Spacer(Modifier.width(12.dp)); Column { Text("Avisos publicados", fontWeight = FontWeight.Bold); Text("${announcements.size}", style = MaterialTheme.typography.headlineMedium, color = DarkPalette.Secondary) } } } }
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) { Column(modifier = Modifier.padding(20.dp)) { Row { Icon(Icons.Default.HistoryEdu, null, tint = DarkPalette.Info, modifier = Modifier.size(32.dp)); Spacer(Modifier.width(12.dp)); Column { Text("Entradas de bitácora", fontWeight = FontWeight.Bold); Text("${logEntries.size}", style = MaterialTheme.typography.headlineMedium, color = DarkPalette.Info) } } } }
                }
            }
            
            // ============ PANTALLA PRINCIPAL ============
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Cabecera con avatar
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) else LightPalette.Primary.copy(alpha = 0.1f))) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text(directorName.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("👔 $directorName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
                                Text("Panel de Dirección", style = MaterialTheme.typography.bodyMedium, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row { Icon(Icons.Default.Group, null, modifier = Modifier.size(16.dp), tint = DarkPalette.Primary); Text("${teachers.size} docentes", style = MaterialTheme.typography.labelSmall) }
                                    Row { Icon(Icons.Default.Campaign, null, modifier = Modifier.size(16.dp), tint = DarkPalette.Secondary); Text("${announcements.size} avisos", style = MaterialTheme.typography.labelSmall) }
                                }
                            }
                            IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") }
                            IconButton(onClick = onNavigateBack) { Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error) }
                        }
                    }
                    
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { MenuCard("📢 Avisos\nDocentes", Icons.Default.Campaign, "${announcements.size} publicados", DarkPalette.Primary, DarkPalette.PrimaryVariant) { message = ""; currentScreen = "announcements" } }
                        item { MenuCard("➕ Registrar\nDocente", Icons.Default.PersonAdd, "Nuevo código", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { message = ""; generatedCode = ""; currentScreen = "register_teacher" } }
                        item { MenuCard("👨‍🏫 Ver\nDocentes", Icons.Default.Group, "${teachers.size} activos", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }; currentScreen = "view_teachers" } }
                        item { MenuCard("📋 Bitácora\nDirector", Icons.Default.HistoryEdu, "${logEntries.size} entradas", DarkPalette.Info, DarkPalette.InfoContainer) { message = ""; currentScreen = "logbook" } }
                        item { MenuCard("📊 Reportes\nEstadísticas", Icons.Default.Assessment, "Ver datos", DarkPalette.MoodCalm, DarkPalette.Success) { currentScreen = "reports" } }
                        item { MenuCard("🚪 Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para dar de baja
    if (showDeleteDialog && teacherToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Dar de baja a ${teacherToDelete!!.name}?") },
            text = { Text("El docente ya no podrá acceder al sistema.") },
            confirmButton = { Button(onClick = { repo.deactivateUser(teacherToDelete!!.code); teachers = repo.getAllUsers().filter { it.role == Constants.ROLE_TEACHER }; showDeleteDialog = false; message = "✅ Docente dado de baja" }, colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Error)) { Text("Dar de baja") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun MenuCard(title: String, icon: ImageVector, desc: String, c1: androidx.compose.ui.graphics.Color, c2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center); Text(desc, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center) }
        }
    }
}
