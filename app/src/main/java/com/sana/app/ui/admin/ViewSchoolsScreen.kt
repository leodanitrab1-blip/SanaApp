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
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.SchoolRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

@Composable
fun ViewSchoolsScreen(schools: List<SchoolRecord>, repo: FirebaseRepository, isDark: Boolean, onBack: () -> Unit, onRefresh: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Escuelas (${schools.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp)); Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
        if (schools.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay escuelas", color = DarkPalette.TextMuted) } }
        else { LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(schools) { s -> Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) { Column(modifier = Modifier.padding(16.dp)) { Text("🏫 ${s.name}", fontWeight = FontWeight.Bold); Text("📝 ${s.code}"); Text("👔 ${s.adminCode}"); Text("👨‍🏫 ${s.teacherCount} docentes") } } } } }
    }
}
