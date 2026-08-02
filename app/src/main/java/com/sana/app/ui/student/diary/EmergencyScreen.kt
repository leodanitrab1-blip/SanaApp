package com.sana.app.ui.student.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.database.entities.EmergencyContactEntity
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import com.sana.app.core.utils.dialPhone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmergencyViewModel = hiltViewModel(),
    themeManager: ThemeManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 40) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("🆘 Líneas de Ayuda", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.ErrorContainer.copy(alpha = 0.6f))
            )

            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.Error.copy(alpha = 0.1f))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, null, tint = DarkPalette.Error, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("No estás solo/a. Todas estas líneas son gratuitas, confidenciales y disponibles 24/7.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            val contactsByCountry = uiState.contacts.groupBy { it.country }
            
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                contactsByCountry.forEach { (country, contacts) ->
                    item(key = "h_$country") {
                        Text(country, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                    }
                    items(contacts, key = { "c_${it.id}" }) { contact ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { if (contact.phone.isNotEmpty()) context.dialPhone(contact.phone) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = DarkPalette.Error.copy(alpha = 0.15f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Phone, null, tint = DarkPalette.Error, modifier = Modifier.size(24.dp)) }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(contact.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface)
                                    contact.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted) }
                                    if (contact.phone.isNotEmpty()) {
                                        Text(contact.phone, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.Primary else LightPalette.Primary)
                                    }
                                    if (contact.website != null) {
                                        Text(contact.website, style = MaterialTheme.typography.labelSmall, color = DarkPalette.Info)
                                    }
                                }
                                if (contact.phone.isNotEmpty()) {
                                    Icon(Icons.Default.Call, "Llamar", tint = DarkPalette.Success, modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
