package com.sana.app.ui.student.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(userId: Long, onNavigateBack: () -> Unit, viewModel: ChatViewModel = hiltViewModel(), themeManager: ThemeManager) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) { if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(uiState.messages.size - 1) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 50) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Psychology, null, tint = if (isDark) DarkPalette.Primary else LightPalette.Primary); Spacer(Modifier.width(8.dp)); Text("Asistente Emocional", fontWeight = FontWeight.Bold) } }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.9f) else LightPalette.Surface.copy(alpha = 0.9f)))

            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, null, modifier = Modifier.size(64.dp), tint = if (isDark) DarkPalette.Primary else LightPalette.Primary)
                    Spacer(Modifier.height(16.dp))
                    Text("¿Cómo te sientes hoy?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Estoy aquí para escucharte.", style = MaterialTheme.typography.bodyMedium, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant)
                }
            }

            LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.messages) { msg ->
                    val isUser = msg.role == "user"
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                        Card(modifier = Modifier.widthIn(max = 320.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isUser) (if (isDark) DarkPalette.Primary else LightPalette.Primary) else (if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant))) {
                            Text(msg.content, modifier = Modifier.padding(12.dp), color = if (isUser) androidx.compose.ui.graphics.Color.White else (if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface))
                        }
                        Text(msg.timestamp, style = MaterialTheme.typography.labelSmall, color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                if (uiState.isLoading) { item { Text("Escribiendo...", modifier = Modifier.padding(8.dp), color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted) } }
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = if (isDark) DarkPalette.Surface.copy(alpha = 0.95f) else LightPalette.Surface.copy(alpha = 0.95f), shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = messageText, onValueChange = { messageText = it }, modifier = Modifier.weight(1f), placeholder = { Text("Escribe tu mensaje...") }, maxLines = 4, shape = RoundedCornerShape(24.dp))
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { if (messageText.isNotBlank()) { viewModel.sendMessage(userId, messageText.trim()); messageText = "" } }, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(if (messageText.isNotBlank()) (if (isDark) DarkPalette.Primary else LightPalette.Primary) else (if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant))) {
                        Icon(Icons.Default.Send, "Enviar", tint = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }
        }
    }
}
