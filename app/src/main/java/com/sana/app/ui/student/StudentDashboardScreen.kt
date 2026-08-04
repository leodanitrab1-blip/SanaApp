package com.sana.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("main") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 100) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (currentScreen) {
            "chat" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🤖 Chat IA") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.PrimaryContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Psychology, null, modifier = Modifier.size(64.dp), tint = DarkPalette.Primary)
                            Spacer(Modifier.height(16.dp))
                            Text("Chat IA - Escribe tu mensaje", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            var msg by remember { mutableStateOf("") }
                            var response by remember { mutableStateOf("") }
                            OutlinedTextField(value = msg, onValueChange = { msg = it }, modifier = Modifier.fillMaxWidth().padding(16.dp), label = { Text("¿Cómo te sientes?") })
                            Button(onClick = { 
                                response = when {
                                    msg.contains("hola", true) -> "¡Hola! 🌟 ¿Cómo estás?"
                                    msg.contains("triste", true) -> "Siento que estés triste 🤍 No estás solo/a"
                                    msg.contains("ansioso", true) || msg.contains("ansiedad", true) -> "Respira profundo 🌿 Todo estará bien"
                                    msg.contains("feliz", true) -> "¡Me alegra mucho! 😊"
                                    else -> "Estoy aquí para escucharte 💭"
                                }
                                msg = ""
                            }, modifier = Modifier.padding(8.dp)) { Text("Enviar") }
                            if (response.isNotEmpty()) { Card(modifier = Modifier.padding(16.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SurfaceVariant)) { Text(response, modifier = Modifier.padding(16.dp)) } }
                        }
                    }
                }
            }
            "breathing" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🫁 Respiración") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.SecondaryContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ejercicios de respiración guiada", style = MaterialTheme.typography.titleMedium, color = DarkPalette.TextMuted)
                    }
                }
            }
            "diary" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("📝 Diario") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.TertiaryContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tu diario emocional", style = MaterialTheme.typography.titleMedium, color = DarkPalette.TextMuted)
                    }
                }
            }
            "emergency" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🆘 Ayuda") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.ErrorContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📞 Línea de la Vida: 800-911-2000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("📞 SAPTEL: 55-5259-8121", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            "games" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🎮 Juegos") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.InfoContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Juegos educativos", style = MaterialTheme.typography.titleMedium, color = DarkPalette.TextMuted)
                    }
                }
            }
            "library" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("📖 Biblioteca") }, navigationIcon = { IconButton(onClick = { currentScreen = "main" }) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.PrimaryContainer))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Guías de estudio", style = MaterialTheme.typography.titleMedium, color = DarkPalette.TextMuted)
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(title = { Text("🎓 Alumno") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { StudentCard("🤖 Chat IA", Icons.Default.Psychology, DarkPalette.Primary, DarkPalette.PrimaryVariant) { currentScreen = "chat" } }
                        item { StudentCard("🫁 Respiración", Icons.Default.Air, DarkPalette.Secondary, DarkPalette.SecondaryVariant) { currentScreen = "breathing" } }
                        item { StudentCard("📝 Diario", Icons.Default.Book, DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { currentScreen = "diary" } }
                        item { StudentCard("🆘 Ayuda", Icons.Default.Sos, DarkPalette.Error, DarkPalette.ErrorContainer) { currentScreen = "emergency" } }
                        item { StudentCard("🎮 Juegos", Icons.Default.Games, DarkPalette.MoodHappy, DarkPalette.Warning) { currentScreen = "games" } }
                        item { StudentCard("📖 Biblioteca", Icons.Default.LibraryBooks, DarkPalette.Info, DarkPalette.InfoContainer) { currentScreen = "library" } }
                        item { StudentCard("📬 Buzón", Icons.Default.Email, DarkPalette.MoodCalm, DarkPalette.Success) { } }
                        item { StudentCard("🚪 Salir", Icons.Default.Logout, DarkPalette.Warning, DarkPalette.Error) { onNavigateBack() } }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentCard(title: String, icon: ImageVector, c1: androidx.compose.ui.graphics.Color, c2: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center) }
        }
    }
}
