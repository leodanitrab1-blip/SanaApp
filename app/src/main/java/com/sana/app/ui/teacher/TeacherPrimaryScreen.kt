package com.sana.app.ui.teacher

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
import com.sana.app.core.repository.ParentRecord
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.utils.Constants

@Composable
fun TeacherPrimaryScreen(parents: List<UserRecord>, repo: FirebaseRepository, isDark: Boolean, onBack: () -> Unit, onRefresh: () -> Unit) {
    var currentView by remember { mutableStateOf("list") }
    var parentName by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🧒 Primaria", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { currentView = "list"; onRefresh() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "list") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Text("👪 Padres (${parents.size})") }
            Button(onClick = { currentView = "register" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (currentView == "register") DarkPalette.Primary else DarkPalette.SurfaceVariant)) { Text("➕ Registrar") }
        }
        Spacer(Modifier.height(16.dp))
        
        when (currentView) {
            "register" -> {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Registrar Padre de Familia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = parentName, onValueChange = { parentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del padre/madre") }, leadingIcon = { Icon(Icons.Default.Person, null) }, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = studentName, onValueChange = { studentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del alumno") }, leadingIcon = { Icon(Icons.Default.Face, null) }, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            if (parentName.isNotBlank() && studentName.isNotBlank()) {
                                val code = repo.generateCode("PAD")
                                repo.saveUser(UserRecord(code = code, role = Constants.ROLE_PARENT, name = parentName))
                                repo.saveParent(ParentRecord(code = code, name = parentName, studentName = studentName, teacherCode = ""))
                                generatedCode = code; onRefresh(); parentName = ""; studentName = ""
                            }
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Primary)) {
                            Icon(Icons.Default.FamilyRestroom, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Generar Código de Acceso")
                        }
                        if (generatedCode.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CheckCircle, null, tint = DarkPalette.Success, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.height(8.dp)); Text("✅ Código generado", fontWeight = FontWeight.Bold)
                                    Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DarkPalette.Primary)
                                }
                            }
                        }
                    }
                }
            }
            "list" -> {
                if (parents.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay padres registrados", color = DarkPalette.TextMuted) } }
                else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(parents) { p ->
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface)) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(22.dp), color = DarkPalette.Info) { Box(contentAlignment = Alignment.Center) { Text((p.name).take(1).uppercase(), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) } }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold); Text("🔑 ${p.code}", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
