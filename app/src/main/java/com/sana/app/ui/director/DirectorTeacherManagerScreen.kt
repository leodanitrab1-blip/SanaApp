package com.sana.app.ui.director

import androidx.compose.animation.*
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
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.utils.Constants

@Composable
fun DirectorTeacherManagerScreen(teachers: List<UserRecord>, repo: FirebaseRepository, isDark: Boolean, onBack: () -> Unit, onRefresh: () -> Unit, onTeacherRegistered: (String) -> Unit) {
    var currentView by remember { mutableStateOf("list") }
    var teacherName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var teacherToDelete by remember { mutableStateOf<UserRecord?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("👨‍🏫 Gestión Docente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { currentView = "list"; onRefresh() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "list") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Text("Ver (${teachers.size})") }
            Button(onClick = { currentView = "register" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "register") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Text("➕ Nuevo") }
        }
        Spacer(Modifier.height(16.dp))
        
        when (currentView) {
            "register" -> {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Registrar Docente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = teacherName, onValueChange = { teacherName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del docente") }, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { if (teacherName.isNotBlank()) { val c = repo.generateCode("DOC"); repo.saveUser(UserRecord(code = c, role = Constants.ROLE_TEACHER, name = teacherName)); generatedCode = c; onTeacherRegistered(c); teacherName = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) { Text("Generar Código") }
                        if (generatedCode.isNotEmpty()) { Spacer(Modifier.height(16.dp)); Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) { Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("✅ Código:", fontWeight = FontWeight.Bold); Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) } } }
                    }
                }
            }
            "list" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(teachers) { t ->
                        Card(shape = RoundedCornerShape(16.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) { Text(t.name, fontWeight = FontWeight.Bold); Text("🔑 ${t.code}", style = MaterialTheme.typography.bodySmall) }
                                IconButton(onClick = { teacherToDelete = t; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Baja", tint = DarkPalette.Error) }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog && teacherToDelete != null) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("¿Dar de baja?") }, text = { Text("El docente no podrá acceder.") }, confirmButton = { Button(onClick = { repo.deactivateUser(teacherToDelete!!.code); showDeleteDialog = false; onRefresh() }, colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Error)) { Text("Dar de baja") } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } })
    }
}
