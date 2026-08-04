package com.sana.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sana.app.core.ai.SanaAI
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(userId: Long, onBack: () -> Unit, isDark: Boolean) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sanaAI = remember { SanaAI(context) }
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(Pair("assistant", "¡Hola! 🌟 Soy Sana, tu asistente emocional. ¿Cómo te sientes hoy?"))) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val uid = userId.toString()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("🤖 Sana AI", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.PrimaryContainer)
        )
        
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val s = messages.size
            for (i in 0 until s) {
                val (role, msg) = messages[i]
                item(key = "m_$i") {
                    val isUser = role == "user"
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                        if (!isUser) {
                            Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text("🤖") } }
                            Spacer(Modifier.width(8.dp))
                        }
                        Card(
                            modifier = Modifier.widthIn(max = 260.dp),
                            shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) else RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isUser) DarkPalette.Primary else (if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant))
                        ) {
                            Text(msg, modifier = Modifier.padding(12.dp), color = if (isUser) Color.White else (if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface))
                        }
                    }
                }
            }
            if (isLoading) { item(key = "load") { Row { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("Escribiendo...", color = DarkPalette.TextMuted) } } }
        }
        
        Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("Escribe tu mensaje...") }, shape = RoundedCornerShape(24.dp), maxLines = 3)
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (input.isNotBlank() && !isLoading) {
                            val msg = input.trim()
                            messages = messages + Pair("user", msg)
                            input = ""
                            isLoading = true
                            scope.launch {
                                try {
                                    val response = sanaAI.chat(uid, msg)
                                    messages = messages + Pair("assistant", response)
                                } catch (e: Exception) {
                                    messages = messages + Pair("assistant", "Estoy aquí para ti 💜 Cuéntame más.")
                                }
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(if (input.isNotBlank()) DarkPalette.Primary else DarkPalette.SurfaceVariant),
                    enabled = input.isNotBlank() && !isLoading
                ) { Icon(Icons.Default.Send, "Enviar", tint = Color.White) }
            }
        }
    }
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }
}
