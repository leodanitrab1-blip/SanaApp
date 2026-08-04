package com.sana.app.ui.teacher

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

data class LogbookEntry(val id: Int, val studentName: String, val observation: String, val date: String, val category: String = "GENERAL", val mood: String = "NEUTRAL")
data class StudyPlan(val id: Int, val title: String, val subject: String, val description: String, val date: String, val visibility: String = "PRIVADO")
data class Guide(val id: Int, val title: String, val content: String, val date: String, val sharedWith: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val context = LocalContext.current
    val repo = remember { FirebaseRepository(context.applicationContext) }
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("Docente") }
    
    // Datos
    var logbookEntries by remember { mutableStateOf(listOf<LogbookEntry>()) }
    var studyPlans by remember { mutableStateOf(listOf<StudyPlan>()) }
    var guides by remember { mutableStateOf(listOf<Guide>()) }
    var parents by remember { mutableStateOf(repo.getAllUsers().filter { it.role == Constants.ROLE_PARENT }) }
    var unreadMessages by remember { mutableStateOf(2) }
    
    // Formularios
    var logStudentName by remember { mutableStateOf("") }
    var logObservation by remember { mutableStateOf("") }
    var logCategory by remember { mutableStateOf("GENERAL") }
    var logMood by remember { mutableStateOf("NEUTRAL") }
    var planTitle by remember { mutableStateOf("") }
    var planSubject by remember { mutableStateOf("") }
    var planDescription by remember { mutableStateOf("") }
    var planVisibility by remember { mutableStateOf("PRIVADO") }
    var guideTitle by remember { mutableStateOf("") }
    var guideContent by remember { mutableStateOf("") }
    var guideSharedWith by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var studentNameForParent by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            // ============ BITÁCORA ============
            "logbook" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📋 Bitácora Escolar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nueva observación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = logStudentName, onValueChange = { logStudentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del alumno") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = logObservation, onValueChange = { logObservation = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Observación") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("GENERAL", "ACADÉMICO", "CONDUCTUAL", "EMOCIONAL").forEach { cat ->
                                    FilterChip(selected = logCategory == cat, onClick = { logCategory = cat }, label = { Text(cat) })
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("😊 Feliz" to "HAPPY", "😐 Neutral" to "NEUTRAL", "😢 Triste" to "SAD", "😰 Ansioso" to "ANXIOUS").forEach { (label, code) ->
                                    FilterChip(selected = logMood == code, onClick = { logMood = code }, label = { Text(label) })
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (logStudentName.isNotBlank() && logObservation.isNotBlank()) {
                                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                    logbookEntries = listOf(LogbookEntry(logbookEntries.size, logStudentName, logObservation, date, logCategory, logMood)) + logbookEntries
                                    message = "✅ Observación registrada. Puedes compartirla con otros docentes."
                                    logStudentName = ""; logObservation = ""; logCategory = "GENERAL"; logMood = "NEUTRAL"
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Guardar Observación") }
                        }
                    }
                    
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Text(message, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
                    Spacer(Modifier.height(16.dp))
                    Text("Registros (${logbookEntries.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    if (logbookEntries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bitácora vacía", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = logbookEntries.size
                            for (i in 0 until size) {
                                val entry = logbookEntries[i]
                                item(key = "log_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("👤 ${entry.studentName}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                                Text(when (entry.mood) { "HAPPY" -> "😊"; "CALM" -> "😌"; "SAD" -> "😢"; "ANXIOUS" -> "😰"; else -> "😐" })
                                                Text(entry.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text(entry.observation, style = MaterialTheme.typography.bodyMedium)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Surface(shape = RoundedCornerShape(4.dp), color = DarkPalette.Primary.copy(alpha = 0.1f)) { Text(entry.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
                                                IconButton(onClick = { message = "📤 Bitácora compartida con docentes" }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Share, "Compartir", tint = DarkPalette.Primary, modifier = Modifier.size(16.dp)) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ PLANES DE ESTUDIO ============
            "plans" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎯 Planes de Estudio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Crear plan de estudio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = planTitle, onValueChange = { planTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = planSubject, onValueChange = { planSubject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Materia") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = planDescription, onValueChange = { planDescription = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Descripción") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("PRIVADO" to "🔒 Privado", "PUBLICO" to "🌍 Público", "COMPARTIDO" to "📤 Compartido").forEach { (v, l) ->
                                    FilterChip(selected = planVisibility == v, onClick = { planVisibility = v }, label = { Text(l) })
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (planTitle.isNotBlank() && planSubject.isNotBlank()) {
                                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                    studyPlans = listOf(StudyPlan(studyPlans.size, planTitle, planSubject, planDescription, date, planVisibility)) + studyPlans
                                    message = if (planVisibility == "PUBLICO") "✅ Plan público - visible para todos los alumnos" else if (planVisibility == "COMPARTIDO") "✅ Plan compartido con otros docentes" else "✅ Plan privado guardado"
                                    planTitle = ""; planSubject = ""; planDescription = ""; planVisibility = "PRIVADO"
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Secondary)) { Text("Crear Plan") }
                        }
                    }
                    
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Text(message, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
                    Spacer(Modifier.height(16.dp))
                    Text("Planes (${studyPlans.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    if (studyPlans.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay planes", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = studyPlans.size
                            for (i in 0 until size) {
                                val plan = studyPlans[i]
                                item(key = "plan_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row { Text(plan.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Surface(shape = RoundedCornerShape(4.dp), color = when (plan.visibility) { "PUBLICO" -> DarkPalette.Success.copy(alpha = 0.2f); "COMPARTIDO" -> DarkPalette.Info.copy(alpha = 0.2f); else -> DarkPalette.Warning.copy(alpha = 0.2f) }) { Text(plan.visibility, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) } }
                                            Text("📚 ${plan.subject} - ${plan.date}", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                                            Text(plan.description, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ GUÍAS ============
            "guides" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📤 Guías de Estudio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Publicar guía", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = guideTitle, onValueChange = { guideTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = guideContent, onValueChange = { guideContent = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Contenido") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = guideSharedWith, onValueChange = { guideSharedWith = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Compartir con (código docente/PAD)") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (guideTitle.isNotBlank()) {
                                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                    guides = listOf(Guide(guides.size, guideTitle, guideContent, date, guideSharedWith)) + guides
                                    message = if (guideSharedWith.isNotBlank()) "✅ Guía compartida con $guideSharedWith" else "✅ Guía publicada"
                                    guideTitle = ""; guideContent = ""; guideSharedWith = ""
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Tertiary)) { Text("Publicar Guía") }
                        }
                    }
                    
                    if (message.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Text(message, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
                    Spacer(Modifier.height(16.dp))
                    Text("Guías (${guides.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    if (guides.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay guías", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = guides.size
                            for (i in 0 until size) {
                                val guide = guides[i]
                                item(key = "guide_$i") {
                                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row { Text(guide.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text(guide.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) }
                                            Text(guide.content, style = MaterialTheme.typography.bodyMedium)
                                            if (guide.sharedWith.isNotBlank()) { Text("📤 Compartido con: ${guide.sharedWith}", style = MaterialTheme.typography.labelSmall, color = DarkPalette.Info) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ PRIMARIA (PADRES) ============
            "primary" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🧒 Primaria - Padres", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Registrar padre de familia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("El padre recibirá un código PAD para acceder", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = parentName, onValueChange = { parentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del padre/madre") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = studentNameForParent, onValueChange = { studentNameForParent = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del alumno") }, shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (parentName.isNotBlank() && studentNameForParent.isNotBlank()) {
                                    val code = repo.generateCode("PAD")
                                    repo.saveUser(UserRecord(code = code, role = Constants.ROLE_PARENT, name = parentName))
                                    generatedCode = code
                                    message = "✅ Padre registrado. Código: $code"
                                    parents = repo.getAllUsers().filter { it.role == Constants.ROLE_PARENT }
                                    parentName = ""; studentNameForParent = ""
                                }
                            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Info)) { Text("Generar Código PAD") }
                            
                            if (generatedCode.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("✅ Código generado", fontWeight = FontWeight.Bold)
                                        Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkPalette.Primary)
                                        Text("Entrega este código al padre", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Text("Padres registrados (${parents.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (parents.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay padres registrados", color = DarkPalette.TextMuted) } }
                    else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val size = parents.size
                            for (i in 0 until size) {
                                val p = parents[i]
                                item(key = "parent_$i") {
                                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = DarkPalette.Info) { Box(contentAlignment = Alignment.Center) { Text(p.name.take(1).uppercase(), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold); Text(p.code, style = MaterialTheme.typography.bodySmall, color = DarkPalette.Primary) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // ============ BUZÓN ============
            "messages" -> {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Row { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📬 Buzón de Mensajes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(20.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Campaign, null, tint = DarkPalette.Primary, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(12.dp)); Column { Text("Aviso del director", fontWeight = FontWeight.Bold); Text("Reunión general este viernes a las 10am", style = MaterialTheme.typography.bodySmall) } }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Share, null, tint = DarkPalette.Secondary, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(12.dp)); Column { Text("Bitácora compartida", fontWeight = FontWeight.Bold); Text("Docente Juan compartió observaciones", style = MaterialTheme.typography.bodySmall) } }
                        }
                    }
                    Text("${unreadMessages} mensajes sin leer", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted, modifier = Modifier.padding(top = 8.dp))
                }
            }
            
            // ============ PANTALLA PRINCIPAL ============
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Cabecera con avatar
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) else LightPalette.Primary.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text(teacherName.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("👨‍🏫 $teacherName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
                                    Text("Panel Docente", style = MaterialTheme.typography.bodyMedium, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant)
                                }
                                IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") }
                                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error) }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                                StatBadge("Bitácora", logbookEntries.size, DarkPalette.Primary)
                                StatBadge("Planes", studyPlans.size, DarkPalette.Secondary)
                                StatBadge("Guías", guides.size, DarkPalette.Tertiary)
                                StatBadge("Padres", parents.size, DarkPalette.Info)
                            }
                        }
                    }
                    
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { MenuCard("📋 Bitácora\nEscolar", Icons.Default.EditNote, "${logbookEntries.size} registros", DarkPalette.Primary, DarkPalette.PrimaryVariant) { message = ""; currentScreen = "logbook" } }
                        item { MenuCard("🎯 Planes de\nEstudio", Icons.Default.Plumbing, "${studyPlans.size} planes", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { message = ""; currentScreen = "plans" } }
                        item { MenuCard("📤 Guías\nDidácticas", Icons.Default.UploadFile, "${guides.size} guías", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { message = ""; currentScreen = "guides" } }
                        item { MenuCard("🧒 Primaria\nPadres", Icons.Default.Toys, "${parents.size} padres", DarkPalette.Info, DarkPalette.InfoContainer) { message = ""; currentScreen = "primary" } }
                        item { MenuCard("📬 Buzón\nMensajes", Icons.Default.Mail, "$unreadMessages sin leer", DarkPalette.MoodCalm, DarkPalette.Success) { currentScreen = "messages" } }
                        item { MenuCard("🚪 Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.2f)) { Text("$count", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted)
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
