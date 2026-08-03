package com.sana.app.ui.director

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

data class ReportCard(val title: String, val value: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val color: androidx.compose.ui.graphics.Color, val trend: String = "")

@Composable
fun DirectorReportsScreen(
    teacherCount: Int,
    announcementCount: Int,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val reports = listOf(
        ReportCard("Docentes activos", "$teacherCount", Icons.Default.Group, DarkPalette.Primary, "+${teacherCount} este mes"),
        ReportCard("Avisos publicados", "$announcementCount", Icons.Default.Campaign, DarkPalette.Secondary),
        ReportCard("Tasa de retención", "100%", Icons.Default.TrendingUp, DarkPalette.Success, "Estable"),
        ReportCard("Reportes enviados", "0", Icons.Default.Email, DarkPalette.Info, "Próximamente")
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
            Text("📊 Reportes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reports.size) { index ->
                val r = reports[index]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(16.dp), color = r.color.copy(alpha = 0.2f)) {
                            Box(contentAlignment = Alignment.Center) { Icon(r.icon, null, tint = r.color, modifier = Modifier.size(28.dp)) }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(r.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(r.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = r.color)
                        }
                        if (r.trend.isNotEmpty()) {
                            Surface(shape = RoundedCornerShape(8.dp), color = DarkPalette.Success.copy(alpha = 0.2f)) {
                                Text(r.trend, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = DarkPalette.Success)
                            }
                        }
                    }
                }
            }
        }
    }
}
