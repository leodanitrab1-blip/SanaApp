package com.sana.app.ui.student

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DiaryEntry(val id: Int, val mood: String, val title: String, val content: String, val date: String)
data class BreathExercise(val id: Int, val name: String, val desc: String, val inhale: Int, val hold: Int, val exhale: Int, val rounds: Int)
data class EmerContact(val id: Int, val name: String, val phone: String, val desc: String, val country: String)
data class GameItem(val title: String, val icon: ImageVector, val desc: String)

@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("Alumno") }
    var diaryEntries by remember { mutableStateOf(listOf<DiaryEntry>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 100) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (screen) {
            // ============ RESPIRACIÓN ============
            "breathing" -> BreathingScreen(isDark) { screen = "main" }
            
            // ============ DIARIO EMOCIONAL ============
            "diary" -> DiaryScreen(diaryEntries, isDark,
                onSave = { mood, title, content ->
                    diaryEntries = listOf(DiaryEntry(diaryEntries.size, mood, title, content, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))) + diaryEntries
                },
                onDelete = { id -> diaryEntries = diaryEntries.filter { it.id != id } }
            ) { screen = "main" }
            
            // ============ LÍNEAS DE EMERGENCIA ============
            "emergency" -> EmergencyScreen(isDark) { screen = "main" }
            
            // ============ JUEGOS ============
            "games" -> GamesScreen(isDark) { screen = "main" }
            
            // ============ BIBLIOTECA ============
            "library" -> LibraryScreen(isDark) { screen = "main" }
            
            // ============ PANTALLA PRINCIPAL ============
            else -> MainScreen(studentName, diaryEntries.size, isDark,
                onNavigate = { screen = it },
                onLogout = onNavigateBack,
                onToggleTheme = { scope.launch { themeManager.toggleTheme() } }
            )
        }
    }
}

// ============ PANTALLA PRINCIPAL ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(name: String, diaryCount: Int, isDark: Boolean, onNavigate: (String) -> Unit, onLogout: () -> Unit, onToggleTheme: () -> Unit) {
    val frases = listOf("🌟 Cada día es una nueva oportunidad", "💪 Eres más fuerte de lo que crees", "🌈 Después de la tormenta sale el sol", "🦋 Respira profundo, todo estará bien")
    var fraseIdx by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Primary.copy(alpha = 0.15f) else LightPalette.Primary.copy(alpha = 0.1f))) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text(name.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold) } }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) { Text("🎓 $name", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground); Text("Tu espacio seguro", style = MaterialTheme.typography.bodyMedium) }
                    IconButton(onClick = onToggleTheme) { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, "Tema") }
                    IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Salir", tint = DarkPalette.Error) }
                }
                Spacer(Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.Primary.copy(alpha = 0.08f))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text("💭", style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.width(8.dp)); Text(frases[fraseIdx], style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f)); IconButton(onClick = { fraseIdx = (fraseIdx + 1) % frases.size }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Refresh, "Otra", modifier = Modifier.size(16.dp), tint = DarkPalette.Primary) } }
                }
            }
        }
        
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { MenuCard("🤖 Chat IA", Icons.Default.Psychology, "Asistente emocional", DarkPalette.Primary, DarkPalette.PrimaryVariant) { /* Se hará aparte */ } }
            item { MenuCard("🫁 Respiración", Icons.Default.Air, "7 ejercicios guiados", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { onNavigate("breathing") } }
            item { MenuCard("📝 Diario", Icons.Default.Book, "$diaryCount entradas", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { onNavigate("diary") } }
            item { MenuCard("🆘 Ayuda", Icons.Default.Sos, "Líneas de emergencia", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigate("emergency") } }
            item { MenuCard("🎮 Juegos", Icons.Default.Games, "Aprende jugando", DarkPalette.MoodHappy, DarkPalette.Warning) { onNavigate("games") } }
            item { MenuCard("📖 Biblioteca", Icons.Default.LibraryBooks, "Guías y material", DarkPalette.Info, DarkPalette.InfoContainer) { onNavigate("library") } }
            item { MenuCard("📚 Planes", Icons.Default.Assignment, "De tus docentes", DarkPalette.MoodCalm, DarkPalette.Success) { } }
            item { MenuCard("🚪 Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Warning, DarkPalette.Error) { onLogout() } }
        }
    }
}

// ============ RESPIRACIÓN ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BreathingScreen(isDark: Boolean, onBack: () -> Unit) {
    val exercises = listOf(BreathExercise(1, "4-7-8", "Inhala 4s, retén 7s, exhala 8s", 4, 7, 8, 4), BreathExercise(2, "Cuadrada", "4s cada fase", 4, 4, 4, 5), BreathExercise(3, "Calma Rápida", "Inhala 3s, exhala 6s", 3, 0, 6, 5), BreathExercise(4, "Energía", "Inhala 6s, exhala 2s", 6, 0, 2, 8), BreathExercise(5, "Anti-Ansiedad", "5-5-10", 5, 5, 10, 3), BreathExercise(6, "Sueño", "4-7-8 x10", 4, 7, 8, 10), BreathExercise(7, "Consciente", "Libre con guía", 5, 0, 5, 3))
    var selected by remember { mutableStateOf<BreathExercise?>(null) }
    var playing by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("ready") }
    var secs by remember { mutableStateOf(3) }
    var round by remember { mutableStateOf(1) }
    val ctx = LocalContext.current

    if (selected == null) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🫁 Respiración Guiada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val s = exercises.size
                for (i in 0 until s) { val ex = exercises[i]; item(key = "ex_$i") { Card(onClick = { selected = ex }, shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = DarkPalette.Primary.copy(alpha = 0.2f)) { Box(contentAlignment = Alignment.Center) { Text("${ex.id}", fontWeight = FontWeight.Bold, color = DarkPalette.Primary) } }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(ex.name, fontWeight = FontWeight.Bold); Text(ex.desc, style = MaterialTheme.typography.bodySmall); Text("${ex.rounds} rondas", style = MaterialTheme.typography.labelSmall, color = DarkPalette.Primary) }; Icon(Icons.Default.PlayArrow, "Iniciar", tint = DarkPalette.Primary) } } } }
            }
        }
    } else {
        val ex = selected!!
        val scaleAnim by animateFloatAsState(targetValue = when (phase) { "inhale" -> 1.5f; "exhale" -> 0.6f; else -> 1f }, animationSpec = tween(1000, easing = EaseInOutCubic), label = "s")
        val pColor by animateColorAsState(targetValue = when (phase) { "inhale" -> DarkPalette.Primary; "hold" -> DarkPalette.Tertiary; "exhale" -> DarkPalette.Secondary; else -> DarkPalette.OnSurface }, animationSpec = tween(500), label = "c")
        LaunchedEffect(playing, round) { if (playing) { for (r in round..ex.rounds) { round = r; phase = "inhale"; secs = ex.inhale; for (s in ex.inhale downTo 1) { delay(1000); secs = s }; if (ex.hold > 0) { phase = "hold"; secs = ex.hold; for (s in ex.hold downTo 1) { delay(1000); secs = s } }; phase = "exhale"; secs = ex.exhale; for (s in ex.exhale downTo 1) { delay(1000); secs = s } }; phase = "completed"; playing = false; try { MediaPlayer.create(ctx, android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION))?.start() } catch (_: Exception) { } } }

        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(ex.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(40.dp))
            Box(modifier = Modifier.size(200.dp).scale(scaleAnim), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(color = pColor.copy(alpha = 0.2f), radius = size.minDimension / 2, style = Fill); drawCircle(color = pColor, radius = size.minDimension / 2, style = Stroke(width = 6.dp.toPx())); val a = (System.currentTimeMillis() % 5000) / 5000f * 360f; drawCircle(color = pColor.copy(alpha = 0.6f), radius = 8.dp.toPx(), center = Offset(size.width/2 + (size.minDimension/2.5f) * kotlin.math.cos(Math.toRadians(a.toDouble())).toFloat(), size.height/2 + (size.minDimension/2.5f) * kotlin.math.sin(Math.toRadians(a.toDouble())).toFloat())) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(when (phase) { "inhale" -> "🌬️ Inhala"; "hold" -> "🫁 Retén"; "exhale" -> "💨 Exhala"; "completed" -> "✅"; else -> "✨" }, fontWeight = FontWeight.Bold, color = pColor); Text("$secs", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = pColor) }
            }
            Spacer(Modifier.height(40.dp)); Text("Ronda $round/${ex.rounds}"); LinearProgressIndicator(progress = round.toFloat() / ex.rounds, modifier = Modifier.fillMaxWidth(0.6f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = pColor)
            Spacer(Modifier.height(48.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) { OutlinedButton(onClick = { selected = null; playing = false }, shape = CircleShape, modifier = Modifier.size(64.dp)) { Icon(Icons.Default.Close, "Salir") }; Button(onClick = { if (phase == "completed") { round = 1; phase = "ready"; secs = 3 }; playing = !playing }, shape = CircleShape, modifier = Modifier.size(80.dp), colors = ButtonDefaults.buttonColors(containerColor = pColor)) { Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, "Iniciar", modifier = Modifier.size(40.dp), tint = Color.White) }; Spacer(Modifier.size(64.dp)) }
        }
    }
}

// ============ DIARIO ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryScreen(entries: List<DiaryEntry>, isDark: Boolean, onSave: (String, String, String) -> Unit, onDelete: (Int) -> Unit, onBack: () -> Unit) {
    var mood by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val moods = listOf("😊 Feliz" to "HAPPY", "😌 Tranquilo" to "CALM", "😐 Neutral" to "NEUTRAL", "😢 Triste" to "SAD", "😰 Ansioso" to "ANXIOUS", "😠 Enojado" to "ANGRY")
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📝 Diario (${entries.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Text("¿Cómo te sientes?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { moods.forEach { (l, c) -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { mood = c }) { Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = if (mood == c) DarkPalette.Primary else DarkPalette.SurfaceVariant) { Box(contentAlignment = Alignment.Center) { Text(l.split(" ").first(), style = MaterialTheme.typography.headlineSmall) } } } } }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Escribe lo que sientes...") }, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(12.dp))
        Button(onClick = { if (mood.isNotEmpty() && content.isNotBlank()) { onSave(mood, title.ifBlank { "Sin título" }, content); mood = ""; title = ""; content = "" } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Tertiary)) { Text("💾 Guardar") }
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Diario vacío", color = DarkPalette.TextMuted) } }
        else { LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) { val s = entries.size; for (i in 0 until s) { val e = entries[i]; item(key = "d_$i") { Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) { Column(modifier = Modifier.padding(16.dp)) { Row { Text(moods.find { it.second == e.mood }?.first?.split(" ")?.first() ?: "😐", style = MaterialTheme.typography.headlineMedium); Spacer(Modifier.width(8.dp)); Column { Text(e.title, fontWeight = FontWeight.Bold); Text(e.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) } }; Spacer(Modifier.height(8.dp)); Text(e.content) } } } } } }
    }
}

// ============ EMERGENCIA ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyScreen(isDark: Boolean, onBack: () -> Unit) {
    val contacts = listOf(EmerContact(1, "Línea de la Vida", "800-911-2000", "Crisis 24/7", "México"), EmerContact(2, "SAPTEL", "55-5259-8121", "Apoyo psicológico", "México"), EmerContact(3, "Línea 135", "135", "Suicida", "Argentina"), EmerContact(4, "Línea 106", "106", "Salud mental", "Colombia"), EmerContact(5, "Teléfono Esperanza", "717-003-717", "Apoyo", "España"), EmerContact(6, "Línea 024", "024", "Suicida", "España"), EmerContact(7, "Salud Responde", "600-360-7777", "Orientación", "Chile"), EmerContact(8, "Línea 113", "113", "Salud mental", "Perú"), EmerContact(9, "Crisis Text", "741741", "Envía HOME", "Internacional"))
    val ctx = LocalContext.current
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🆘 Ayuda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.Error.copy(alpha = 0.1f))) { Row(modifier = Modifier.padding(16.dp)) { Icon(Icons.Default.Favorite, null, tint = DarkPalette.Error, modifier = Modifier.size(32.dp)); Spacer(Modifier.width(12.dp)); Text("Líneas gratuitas y confidenciales", style = MaterialTheme.typography.bodyMedium) } }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contacts.groupBy { it.country }.forEach { (country, list) ->
                item(key = "h_$country") { Text(country, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                val s = list.size; for (i in 0 until s) { val c = list[i]; item(key = "c_${country}_$i") { Card(modifier = Modifier.fillMaxWidth().clickable { try { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${c.phone}"))) } catch (_: Exception) { } }, shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = DarkPalette.Error.copy(alpha = 0.15f)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Phone, null, tint = DarkPalette.Error) } }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(c.name, fontWeight = FontWeight.Bold); Text(c.desc, style = MaterialTheme.typography.bodySmall); Text(c.phone, fontWeight = FontWeight.Bold, color = DarkPalette.Primary, style = MaterialTheme.typography.titleMedium) }; Icon(Icons.Default.Call, "Llamar", tint = DarkPalette.Success) } } } }
            }
        }
    }
}

// ============ JUEGOS ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamesScreen(isDark: Boolean, onBack: () -> Unit) {
    val games = listOf(GameItem("Memoria", Icons.Default.GridView, "Encuentra las parejas"), GameItem("Snake", Icons.Default.TrendingUp, "La serpiente clásica"), GameItem("Quiz", Icons.Default.Quiz, "Preguntas y respuestas"))
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎮 Juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = games.size; for (i in 0 until s) { val g = games[i]; item(key = "g_$i") { Card(shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(g.icon, null, tint = DarkPalette.Primary, modifier = Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(g.title, fontWeight = FontWeight.Bold); Text(g.desc, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.PlayArrow, "Jugar", tint = DarkPalette.Success) } } } } }
    }
}

// ============ BIBLIOTECA ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(isDark: Boolean, onBack: () -> Unit) {
    val items = listOf("Matemáticas - Álgebra básica" to "DOC-A1B2C3", "Español - Comprensión lectora" to "DOC-X9Y8Z7", "Ciencias - El sistema solar" to "DOC-M4N5O6")
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📖 Biblioteca", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = items.size; for (i in 0 until s) { val (title, author) = items[i]; item(key = "lib_$i") { Card(shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.MenuBook, null, tint = DarkPalette.Primary, modifier = Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text("Por: $author", style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.Download, "Descargar", tint = DarkPalette.Info) } } } } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuCard(title: String, icon: ImageVector, desc: String, c1: Color, c2: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(44.dp), tint = Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center); Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center) }
        }
    }
}
