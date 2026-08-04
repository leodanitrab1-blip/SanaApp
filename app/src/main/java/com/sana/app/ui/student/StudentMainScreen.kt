package com.sana.app.ui.student

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette

data class StudentMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val color1: androidx.compose.ui.graphics.Color,
    val color2: androidx.compose.ui.graphics.Color,
    val emoji: String = ""
)

@Composable
fun StudentMainScreen(
    diaryCount: Int,
    breathingSessions: Int,
    isDark: Boolean,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
    studentName: String = "Alumno"
) {
    // Animación de flotación suave para el avatar
    val floatAnimation = rememberInfiniteTransition(label = "float")
    val floatOffset by floatAnimation.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = EaseInOutCubic), repeatMode = RepeatMode.Reverse),
        label = "floatOffset"
    )

    val menuItems = listOf(
        StudentMenuItem("chat", "Chat IA", Icons.Default.Psychology, "Asistente emocional", DarkPalette.Primary, DarkPalette.PrimaryVariant, "🤖"),
        StudentMenuItem("breathing", "Respiración", Icons.Default.Air, "Ejercicios guiados", DarkPalette.Secondary, DarkPalette.SecondaryVariant, "🫁"),
        StudentMenuItem("diary", "Diario", Icons.Default.Book, "Registro emocional", DarkPalette.Tertiary, DarkPalette.TertiaryContainer, "📝"),
        StudentMenuItem("plans", "Planes", Icons.Default.Assignment, "Planes de estudio", DarkPalette.Info, DarkPalette.InfoContainer, "📚"),
        StudentMenuItem("emergency", "Ayuda", Icons.Default.Sos, "Líneas de emergencia", DarkPalette.Error, DarkPalette.ErrorContainer, "🆘"),
        StudentMenuItem("inbox", "Buzón", Icons.Default.Email, "Mensajes y guías", DarkPalette.MoodCalm, DarkPalette.Success, "📬"),
        StudentMenuItem("games", "Juegos", Icons.Default.Games, "Aprende jugando", DarkPalette.MoodHappy, DarkPalette.Warning, "🎮"),
        StudentMenuItem("library", "Biblioteca", Icons.Default.LibraryBooks, "Guías y material", DarkPalette.MoodAnxious, DarkPalette.Tertiary, "📖")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // CABECERA MÁGICA
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) 
                                else LightPalette.Primary.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar flotante
                    Surface(
                        modifier = Modifier.offset(y = floatOffset.dp).size(60.dp),
                        shape = CircleShape,
                        color = DarkPalette.Primary,
                        shadowElevation = 12.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(studentName.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🎓 $studentName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
                        Text("Tu espacio seguro de bienestar", style = MaterialTheme.typography.bodyMedium, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant)
                    }
                    IconButton(onClick = onToggleTheme) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema", tint = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant) }
                    IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error) }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // FRASE MOTIVACIONAL DIARIA
                val frases = listOf("🌟 Cada día es una nueva oportunidad", "💪 Eres más fuerte de lo que crees", "🌈 Después de la tormenta sale el sol", "🦋 Respira profundo, todo estará bien")
                var fraseIndex by remember { mutableStateOf(0) }
                
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.Primary.copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("💭", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.width(8.dp))
                        Text(frases[fraseIndex], style = MaterialTheme.typography.bodySmall, color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface, modifier = Modifier.weight(1f))
                        IconButton(onClick = { fraseIndex = (fraseIndex + 1) % frases.size }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Refresh, "Otra frase", modifier = Modifier.size(16.dp), tint = DarkPalette.Primary) }
                    }
                }
            }
        }

        // CUADRÍCULA DE SERVICIOS
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems.size) { index ->
                val item = menuItems[index]
                StudentCard(item = item, onClick = { onNavigate(item.id) })
            }
        }
    }
}

@Composable
private fun StudentCard(item: StudentMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(item.color1, item.color2))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(item.icon, item.title, modifier = Modifier.size(48.dp), tint = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(12.dp))
                Text(item.emoji + " " + item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(item.description, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            }
        }
    }
}
