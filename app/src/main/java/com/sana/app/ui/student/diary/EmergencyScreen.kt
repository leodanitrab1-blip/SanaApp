package com.sana.app.ui.student.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    val bgColors = if (isDark) listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)
                   else listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(bgColors)))
        if (isDark) StarryBackground(starColor = DarkPalette.StarDim, starCount = 40)

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(text = "Líneas de Ayuda", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.ErrorContainer.copy(alpha = 0.5f)
                                    else LightPalette.ErrorContainer.copy(alpha = 0.5f)
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DarkPalette.ErrorContainer.copy(alpha = 0.3f)
                                    else LightPalette.ErrorContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = DarkPalette.Error, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "No estás solo/a. Todas estas líneas son gratuitas y confidenciales.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Lista de contactos
            val contactsByCountry: Map<String, List<EmergencyContactEntity>> = uiState.contacts.groupBy { it.country }
            val countries: List<String> = contactsByCountry.keys.toList()

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                countries.forEach { country: String ->
                    val contacts: List<EmergencyContactEntity> = contactsByCountry[country] ?: emptyList()

                    item(key = "header_$country") {
                        Text(
                            text = country,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        count = contacts.size,
                        key = { index: Int -> "contact_${country}_$index" }
                    ) { index: Int ->
                        val contact: EmergencyContactEntity = contacts[index]
                        EmergencyContactCard(
                            name = contact.name,
                            phone = contact.phone,
                            description = contact.description ?: "",
                            isDark = isDark,
                            onCall = { context.dialPhone(contact.phone) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyContactCard(
    name: String,
    phone: String,
    description: String,
    isDark: Boolean,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onCall),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = DarkPalette.Error.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = DarkPalette.Error, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                    )
                }
                Text(
                    text = phone,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) DarkPalette.Primary else LightPalette.Primary
                )
            }
            Icon(
                Icons.Default.Call,
                contentDescription = "Llamar",
                tint = DarkPalette.Success,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
