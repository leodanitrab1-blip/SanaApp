package com.sana.app.ui.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import java.text.SimpleDateFormat
import java.util.*

data class StudyPlan(val id: Int, val title: String, val subject: String, val description: String, val date: String, val visibility: String = "PRIVADO")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPlansScreen(plans: List<StudyPlan>, onAddPlan: (String, String, String, String) -> Unit, onDeletePlan: (Int) -> Unit, isDark: Boolean, onBack: () -> Unit) {
    var showForm by remember { mutableStateOf(false) }
    var planTitle by remember { mutableStateOf("") }
    var planSubject by remember { mutableStateOf("") }
    var planDescription by remember { mutableStateOf("") }
    var planVisibility by remember { mutableStateOf("PRIVADO") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎯 Planes de Estudio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Surface(shape = RoundedCornerShape(12.dp), color = DarkPalette.Secondary) { Text("${plans.size} planes", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.labelMedium) } }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showForm = !showForm }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (showForm) DarkPalette.Error else DarkPalette.Secondary)) { Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(if (showForm) "Cancelar" else "Crear Plan de Estudio", fontWeight = FontWeight.Bold) }
        AnimatedVisibility(visible = showForm, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nuevo plan de estudio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = planTitle, onValueChange = { planTitle = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título del plan") }, shape = RoundedCornerShape(12.dp)); Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = planSubject, onValueChange = { planSubject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Materia") }, shape = RoundedCornerShape(12.dp)); Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = planDescription, onValueChange = { planDescription = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Descripción del plan") }, shape = RoundedCornerShape(12.dp)); Spacer(Modifier.height(8.dp))
                    Text("Visibilidad:", style = MaterialTheme.typography.labelMedium); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("PÚBLICO" to "PUBLICO", "PRIVADO" to "PRIVADO", "COMPARTIDO" to "COMPARTIDO").forEach { (label, value) -> FilterChip(selected = planVisibility == value, onClick = { planVisibility = value }, label = { Text(label) }) } }; Spacer(Modifier.height(16.dp))
                    Button(onClick = { if (planTitle.isNotBlank() && planSubject.isNotBlank()) { onAddPlan(planTitle, planSubject, planDescription, planVisibility); planTitle = ""; planSubject = ""; planDescription = ""; planVisibility = "PRIVADO"; showForm = false } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Crear Plan 🎯") }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (plans.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Plumbing, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted); Spacer(Modifier.height(16.dp)); Text("No hay planes creados", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted) } } }
        else { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(plans.size) { index -> val plan = plans[index]; Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { Column(modifier = Modifier.padding(16.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(modifier = Modifier.weight(1f)) { Text(plan.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall); Text("📚 ${plan.subject}", style = MaterialTheme.typography.bodySmall, color = DarkPalette.Primary) }; Surface(shape = RoundedCornerShape(8.dp), color = when (plan.visibility) { "PUBLICO" -> DarkPalette.Success.copy(alpha = 0.2f); "COMPARTIDO" -> DarkPalette.Info.copy(alpha = 0.2f); else -> DarkPalette.Warning.copy(alpha = 0.2f) }) { Text(plan.visibility, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall) }; IconButton(onClick = { onDeletePlan(index) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Eliminar", tint = DarkPalette.Error, modifier = Modifier.size(18.dp)) } }; Spacer(Modifier.height(8.dp)); Text(plan.description, style = MaterialTheme.typography.bodyMedium); Text(plan.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) } } } } }
    }
}
