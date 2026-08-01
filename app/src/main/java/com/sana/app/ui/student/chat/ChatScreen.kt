package com.sana.app.ui.student.chat

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

/**
 * 🌿 SANA - Chat con IA (Asistente Emocional)
 * 
 * Pantalla de chat con el asistente de IA usando Groq API.
 * Características:
 * - Conversación en tiempo real con la IA
 * - Historial de mensajes
 * - Respuestas offline (mensajes pregrabados)
 * - Indicador de "escribiendo..."
 * - Tema adaptativo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll al último mensaje
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (isDark) DarkPalette.Primary else LightPalette.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Asistente Emocional",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Siempre aquí para ti 🤍",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) DarkPalette.TextMuted
                                       else LightPalette.TextMuted
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.9f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            // Mensaje de bienvenida si no hay chat
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                WelcomeMessage(isDark = isDark)
            }

            // Lista de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubble(
                        message = message,
                        isDark = isDark
                    )
                }

                // Indicador de escribiendo
                if (uiState.isLoading) {
                    item {
                        TypingIndicator(isDark = isDark)
                    }
                }
            }

            // Mensaje de error
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkPalette.ErrorContainer
                                        else LightPalette.ErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = DarkPalette.Error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = DarkPalette.Error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearError() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Campo de entrada
            ChatInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(userId, messageText.trim())
                        messageText = ""
                    }
                },
                isDark = isDark,
                isLoading = uiState.isLoading
            )
        }
    }
}

/**
 * Mensaje de bienvenida cuando el chat está vacío
 */
@Composable
private fun WelcomeMessage(isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = if (isDark) DarkPalette.Primary else LightPalette.Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¿Cómo te sientes hoy?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Estoy aquí para escucharte. Cuéntame lo que quieras compartir.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
        )
    }
}

/**
 * Burbuja de chat individual
 */
@Composable
private fun ChatBubble(
    message: ChatMessage,
    isDark: Boolean
) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) {
        if (isDark) DarkPalette.Primary else LightPalette.Primary
    } else {
        if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant
    }
    val textColor = if (isUser) {
        androidx.compose.ui.graphics.Color.White
    } else {
        if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

/**
 * Indicador de "escribiendo..."
 */
@Composable
private fun TypingIndicator(isDark: Boolean) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Escribiendo",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        repeat(3) {
            Text(
                text = ".",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) DarkPalette.Primary else LightPalette.Primary
            )
        }
    }
}

/**
 * Campo de entrada de texto del chat
 */
@Composable
private fun ChatInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    isDark: Boolean,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) DarkPalette.Surface.copy(alpha = 0.95f)
               else LightPalette.Surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Escribe tu mensaje...",
                        color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDark) DarkPalette.Primary else LightPalette.Primary,
                    unfocusedBorderColor = if (isDark) DarkPalette.Outline else LightPalette.Outline
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = messageText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (messageText.isNotBlank() && !isLoading)
                            if (isDark) DarkPalette.Primary else LightPalette.Primary
                        else
                            if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}