package com.sana.app.ui.library

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.database.entities.StudyPlanEntity
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import com.sana.app.core.utils.toRelativeDate

/**
 * 🌿 SANA - Biblioteca
 * 
 * Catálogo de guías de estudio y planes públicos.
 * Características:
 * - Guías por materia
 * - Planes públicos visibles para todos
 * - Descarga de PDFs e imágenes
 * - Búsqueda y filtrado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateBack: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkPalette.BackgroundGradientStart,
                                DarkPalette.BackgroundGradientEnd
                            )
                        )
                    )
            )
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 50)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                LightPalette.BackgroundGradientStart,
                                LightPalette.BackgroundGradientEnd
                            )
                        )
                    )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("📚 Biblioteca", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar guías y planes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Lista de materiales
            val filteredPlans = if (searchQuery.isBlank()) {
                uiState.plans
            } else {
                uiState.plans.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.subject.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredPlans.isEmpty()) {
                EmptyLibrary(isDark = isDark)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredPlans) { plan ->
                        LibraryItemCard(plan = plan, isDark = isDark)
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de material de biblioteca
 */
@Composable
private fun LibraryItemCard(
    plan: StudyPlanEntity,
    isDark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de materia
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f)
                       else LightPalette.Primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (plan.subject.lowercase()) {
                            "matemáticas", "matematicas" -> Icons.Default.Calculate
                            "español", "lengua" -> Icons.Default.MenuBook
                            "ciencias" -> Icons.Default.Science
                            "historia" -> Icons.Default.HistoryEdu
                            else -> Icons.Default.LibraryBooks
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isDark) DarkPalette.Primary else LightPalette.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = plan.subject,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) DarkPalette.Primary else LightPalette.Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = plan.timestamp.toRelativeDate(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                    )
                    if (plan.visibility == "PUBLIC") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkPalette.Success.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PÚBLICO",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkPalette.Success
                            )
                        }
                    }
                }
            }

            // Botón descargar
            if (plan.attachmentUrl != null) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Descargar",
                        tint = if (isDark) DarkPalette.Primary else LightPalette.Primary
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío de biblioteca
 */
@Composable
private fun EmptyLibrary(isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LibraryBooks,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Biblioteca vacía",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Los docentes publicarán guías y planes de estudio aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}