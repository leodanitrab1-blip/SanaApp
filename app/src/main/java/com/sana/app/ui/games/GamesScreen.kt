package com.sana.app.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sana.app.core.database.entities.GameEntity
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Pantalla de Juegos
 * 
 * Catálogo de juegos educativos y recreativos.
 * Características:
 * - Categorías: General, Primaria, Secundaria
 * - Vista en cuadrícula con miniaturas
 * - Contador de veces jugado
 * - Calificación por estrellas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onNavigateBack: () -> Unit,
    onGameSelected: (GameEntity) -> Unit,
    viewModel: GamesViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    var selectedCategory by remember { mutableStateOf(Constants.GAME_CATEGORY_GENERAL) }

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
            // Barra superior
            TopAppBar(
                title = { Text("🎮 Juegos", fontWeight = FontWeight.Bold) },
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

            // Pestañas de categorías
            ScrollableTabRow(
                selectedTabIndex = Constants.GAME_CATEGORIES.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.6f)
                                else LightPalette.Surface.copy(alpha = 0.6f),
                edgePadding = 16.dp
            ) {
                Constants.GAME_CATEGORIES.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                text = when (category) {
                                    Constants.GAME_CATEGORY_GENERAL -> "General"
                                    Constants.GAME_CATEGORY_PRIMARY -> "Primaria"
                                    Constants.GAME_CATEGORY_SECONDARY -> "Secundaria"
                                    else -> category
                                },
                                fontWeight = if (selectedCategory == category) FontWeight.Bold
                                       else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Cuadrícula de juegos
            val filteredGames = uiState.games.filter { it.category == selectedCategory }

            if (filteredGames.isEmpty()) {
                // Estado vacío
                EmptyGames(isDark = isDark)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredGames) { game ->
                        GameCard(
                            game = game,
                            isDark = isDark,
                            onClick = { onGameSelected(game) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de juego individual
 */
@Composable
private fun GameCard(
    game: GameEntity,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Miniatura
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        Brush.diagonalGradient(
                            colors = listOf(
                                if (isDark) DarkPalette.Primary.copy(alpha = 0.3f)
                                else LightPalette.Primary.copy(alpha = 0.2f),
                                if (isDark) DarkPalette.Secondary.copy(alpha = 0.3f)
                                else LightPalette.Secondary.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (game.thumbnailUrl != null) {
                    AsyncImage(
                        model = game.thumbnailUrl,
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = when (game.category) {
                            Constants.GAME_CATEGORY_PRIMARY -> Icons.Default.Toys
                            Constants.GAME_CATEGORY_SECONDARY -> Icons.Default.Science
                            else -> Icons.Default.Games
                        },
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isDark) DarkPalette.Primary else LightPalette.Primary
                    )
                }
            }

            // Información
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Veces jugado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${game.playCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                        )
                    }
                    // Estrellas
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = DarkPalette.Warning
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", game.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkPalette.Warning
                        )
                    }
                }
            }
        }
    }
}

/**
 * Estado vacío cuando no hay juegos
 */
@Composable
private fun EmptyGames(isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Games,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay juegos disponibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pronto agregaremos juegos divertidos y educativos",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}