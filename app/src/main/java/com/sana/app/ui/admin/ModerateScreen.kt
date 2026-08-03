package com.sana.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette

@Composable
fun ModerateScreen(isDark: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
            Text("Moderar Contenido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Gavel, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                Spacer(Modifier.height(16.dp))
                Text("Panel de moderación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Próximamente: moderar bitácoras, guías y planes", style = MaterialTheme.typography.bodyMedium, color = DarkPalette.TextMuted)
            }
        }
    }
}
