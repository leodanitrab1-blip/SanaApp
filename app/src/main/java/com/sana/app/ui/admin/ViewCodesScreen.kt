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
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

@Composable
fun ViewCodesScreen(users: List<UserRecord>, repo: FirebaseRepository, isDark: Boolean, onBack: () -> Unit, onRefresh: () -> Unit) {
    var selectedRole by remember { mutableStateOf("ALL") }
    val roles = listOf("ADMIN", "DIRECTOR", "TEACHER", "STUDENT", "PARENT")
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("Códigos (${users.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp)); Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Text("🔄 Actualizar") }
        ScrollableTabRow(selectedTabIndex = if (selectedRole == "ALL") 0 else roles.indexOf(selectedRole) + 1) { Tab(selected = selectedRole == "ALL", onClick = { selectedRole = "ALL" }, text = { Text("Todos") }); roles.forEach { r -> Tab(selected = selectedRole == r, onClick = { selectedRole = r }, text = { Text(r) }) } }
        val filtered = if (selectedRole == "ALL") users else users.filter { it.role == selectedRole }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(filtered) { u -> Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) { Row(modifier = Modifier.padding(16.dp)) { Column(modifier = Modifier.weight(1f)) { Text("🔑 ${u.code}", fontWeight = FontWeight.Bold); Text("👤 ${u.name} (${u.role})") }; Icon(if (u.active) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = if (u.active) DarkPalette.Success else DarkPalette.Error) } } } }
    }
}
