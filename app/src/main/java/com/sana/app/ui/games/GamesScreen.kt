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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(onNavigateBack: () -> Unit, onGameSelected: (GameEntity) -> Unit, viewModel: GamesViewModel = hiltViewModel(), themeManager: ThemeManager) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    var selectedCategory by remember { mutableStateOf(Constants.GAME_CATEGORY_GENERAL) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 50) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("🎮 Juegos", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))

            ScrollableTabRow(selectedTabIndex = Constants.GAME_CATEGORIES.indexOf(selectedCategory), modifier = Modifier.fillMaxWidth(), containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.6f) else LightPalette.Surface.copy(alpha = 0.6f), edgePadding = 16.dp) {
                Constants.GAME_CATEGORIES.forEach { category ->
                    Tab(selected = selectedCategory == category, onClick = { selectedCategory = category }, text = { Text(when (category) { Constants.GAME_CATEGORY_GENERAL -> "General"; Constants.GAME_CATEGORY_PRIMARY -> "Primaria"; Constants.GAME_CATEGORY_SECONDARY -> "Secundaria"; else -> category }) })
                }
            }

            val filteredGames = uiState.games.filter { it.category == selectedCategory }
            if (filteredGames.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Games, null, modifier = Modifier.size(80.dp), tint = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredGames) { game ->
                        Card(modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).clickable { onGameSelected(game) }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(Brush.horizontalGradient(listOf(if (isDark) DarkPalette.Primary.copy(alpha = 0.3f) else LightPalette.Primary.copy(alpha = 0.2f), if (isDark) DarkPalette.Secondary.copy(alpha = 0.3f) else LightPalette.Secondary.copy(alpha = 0.2f)))), contentAlignment = Alignment.Center) {
                                    if (game.thumbnailUrl != null) AsyncImage(model = game.thumbnailUrl, contentDescription = game.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    else Icon(when (game.category) { Constants.GAME_CATEGORY_PRIMARY -> Icons.Default.Toys; Constants.GAME_CATEGORY_SECONDARY -> Icons.Default.Science; else -> Icons.Default.Games }, null, modifier = Modifier.size(48.dp), tint = if (isDark) DarkPalette.Primary else LightPalette.Primary)
                                }
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Text(game.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(game.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Spacer(Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp)); Text("${game.playCount}", style = MaterialTheme.typography.labelSmall) }
                                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = DarkPalette.Warning); Text(String.format("%.1f", game.rating), style = MaterialTheme.typography.labelSmall) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
