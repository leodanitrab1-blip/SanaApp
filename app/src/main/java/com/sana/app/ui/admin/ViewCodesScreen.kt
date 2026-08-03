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
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

@Composable
fun ViewCodesScreen(
    users: List<UserRecord>,
    dataRepository: DataRepository,
    isDark: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("ALL") }
    val roles = listOf("ADMIN", "DIRECTOR", "TEACHER", "STUDENT", "PARENT")
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Códigos de Acceso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Actualizar (${users.size})") }
        Spacer(Modifier.height(8.dp))
        
        ScrollableTabRow(selectedTabIndex = if (selectedRole == "ALL") 0 else roles.indexOf(selectedRole) + 1) {
            Tab(selected = selectedRole == "ALL", onClick = { selectedRole = "ALL" }, text = { Text("Todos") })
            roles.forEach { role -> Tab(selected = selectedRole == role, onClick = { selectedRole = role }, text = { Text(role) }) }
        }
        Spacer(Modifier.height(8.dp))
        
        val filteredUsers = if (selectedRole == "ALL") users else users.filter { it.role == selectedRole }
        
        if (filteredUsers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay códigos para este rol", color = DarkPalette.TextMuted) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredUsers) { user ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = when (user.role) { "ADMIN" -> Icons.Default.AdminPanelSettings; "DIRECTOR" -> Icons.Default.School; "TEACHER" -> Icons.Default.Person; "STUDENT" -> Icons.Default.Face; "PARENT" -> Icons.Default.FamilyRestroom; else -> Icons.Default.Person }, contentDescription = null, tint = DarkPalette.Primary, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) { Text("🔑 ${user.code}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall); Text("👤 ${user.name}", style = MaterialTheme.typography.bodySmall); Text("🎭 ${user.role}", style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) }
                            Surface(shape = RoundedCornerShape(8.dp), color = if (user.active) DarkPalette.Success.copy(alpha = 0.2f) else DarkPalette.Error.copy(alpha = 0.2f)) { Text(if (user.active) "Activo" else "Inactivo", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (user.active) DarkPalette.Success else DarkPalette.Error) }
                        }
                    }
                }
            }
        }
    }
}
