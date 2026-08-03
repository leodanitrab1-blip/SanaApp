package com.sana.app.ui.director

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.utils.Constants

@Composable
fun DirectorTeacherManagerScreen(
    teachers: List<UserRecord>,
    dataRepository: DataRepository,
    isDark: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onTeacherRegistered: (String) -> Unit
) {
    var currentView by remember { mutableStateOf("list") } // list, register
    var teacherName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var teacherToDelete by remember { mutableStateOf<UserRecord?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // CABECERA
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
            Text("👨‍🏫 Gestión Docente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(12.dp))
        
        // TABS
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { currentView = "list"; onRefresh() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "list") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Icon(Icons.Default.Group, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Ver Docentes (${teachers.size})") }
            Button(onClick = { currentView = "register" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "register") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Registrar Nuevo") }
        }
        
        Spacer(Modifier.height(16.dp))
        
        when (currentView) {
            "register" -> {
                // FORMULARIO DE REGISTRO
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Registrar Nuevo Docente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre completo del docente") }, leadingIcon = { Icon(Icons.Default.Person, null) }, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            if (teacherName.isNotBlank()) {
                                val code = dataRepository.generateCode("DOC")
                                dataRepository.saveUser(UserRecord(code = code, role = Constants.ROLE_TEACHER, name = teacherName))
                                generatedCode = code
                                onTeacherRegistered(code)
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
                                    Text("✅ Docente registrado", fontWeight = FontWeight.Bold)
                                    Text("Código de acceso:", style = MaterialTheme.typography.bodySmall)
                                    Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkPalette.Primary)
                                    Spacer(Modifier.height(8.dp))
                                    Text("⚠️ Entrega este código al docente para que pueda acceder al sistema.", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                                }
                            }
                        }
                    }
                }
            }
            
            "list" -> {
                if (teachers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                            Spacer(Modifier.height(16.dp))
                            Text("No hay docentes registrados", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                            Text("Registra tu primer docente", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(teachers) { teacher ->
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Avatar circular
                                        Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(22.dp), color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text(teacher.name.take(1).uppercase(), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(teacher.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            Text("🔑 ${teacher.code}", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                                        }
                                        // Estado
                                        Surface(shape = RoundedCornerShape(8.dp), color = if (teacher.active) DarkPalette.Success.copy(alpha = 0.2f) else DarkPalette.Error.copy(alpha = 0.2f)) {
                                            Text(if (teacher.active) "Activo" else "Inactivo", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (teacher.active) DarkPalette.Success else DarkPalette.Error)
                                        }
                                        IconButton(onClick = { expanded = !expanded }) { Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Detalles") }
                                    }
                                    
                                    // Detalles expandibles
                                    AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                                        Column {
                                            Spacer(Modifier.height(12.dp))
                                            Divider()
                                            Spacer(Modifier.height(12.dp))
                                            Text("📧 Email: ${teacher.name.lowercase().replace(" ", ".")}@escuela.edu", style = MaterialTheme.typography.bodySmall)
                                            Text("📅 Registro: ${teacher.createdAt.ifBlank { "Reciente" }}", style = MaterialTheme.typography.bodySmall)
                                            Spacer(Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedButton(onClick = { teacherToDelete = teacher; showDeleteDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkPalette.Error)) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Dar de baja") }
                                                Button(onClick = { /* Compartir código */ }, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Compartir") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // DIÁLOGO DE CONFIRMACIÓN
    if (showDeleteDialog && teacherToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Dar de baja a ${teacherToDelete!!.name}?") },
            text = { Text("El docente ya no podrá acceder al sistema. Esta acción se puede revertir.") },
            confirmButton = {
                Button(onClick = { dataRepository.deactivateUser(teacherToDelete!!.code); showDeleteDialog = false; onRefresh() }, colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Error)) { Text("Dar de baja") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}
