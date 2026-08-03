package com.sana.app.ui.admin

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
import com.sana.app.core.repository.SchoolRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

@Composable
fun ViewSchoolsScreen(
    schools: List<SchoolRecord>,
    dataRepository: DataRepository,
    isDark: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
            Text("Escuelas Registradas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Actualizar (${schools.size})") }
        Spacer(Modifier.height(12.dp))
        
        if (schools.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay escuelas registradas", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(schools) { school ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.School, null, tint = DarkPalette.Primary, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(8.dp)); Text(school.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
                            Spacer(Modifier.height(8.dp))
                            Text("📝 Código: ${school.code}", style = MaterialTheme.typography.bodySmall)
                            Text("👔 Director: ${school.adminCode} (${school.directorName})", style = MaterialTheme.typography.bodySmall)
                            Text("👨‍🏫 Docentes: ${school.teacherCount}", style = MaterialTheme.typography.bodySmall)
                            if (school.teacherCodes.isNotEmpty()) { school.teacherCodes.forEach { Text("  • $it", style = MaterialTheme.typography.labelSmall) } }
                        }
                    }
                }
            }
        }
    }
}
