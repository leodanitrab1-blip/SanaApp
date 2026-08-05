@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.lazy.rememberLazyListState
import com.sana.app.ui.student.AIChatScreen

data class DiaryEntry(val id: Int, val mood: String, val title: String, val content: String, val date: String)
data class BreathExercise(val id: Int, val name: String, val desc: String, val inhale: Int, val hold: Int, val exhale: Int, val rounds: Int)
data class EmerContact(val id: Int, val name: String, val phone: String, val desc: String, val country: String)

@Composable
fun StudentDashboardScreen(userId: Long, onNavigateBack: () -> Unit, themeManager: ThemeManager) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf("main") }
    var message by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("Alumno") }
    var diaryEntries by remember { mutableStateOf(listOf<DiaryEntry>()) }
    var games by remember { mutableStateOf(listOf<String>()) }
    var plans by remember { mutableStateOf(listOf<String>()) }
    var guides by remember { mutableStateOf(listOf<String>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 100) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        when (screen) {
            "chat" -> { ChatScreenSimple(userId, isDark) { screen = "main" } }
            "breathing" -> BreathingScreen(isDark) { screen = "main" }
            "diary" -> DiaryScreen(diaryEntries, isDark, { m, t, c -> diaryEntries = listOf(DiaryEntry(diaryEntries.size, m, t, c, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))) + diaryEntries }, { id -> diaryEntries = diaryEntries.filter { it.id != id } }) { screen = "main" }
            "emergency" -> EmergencyScreen(isDark) { screen = "main" }
            "games" -> GamesScreen(games, isDark) { screen = "main" }
            "library" -> LibraryScreen(guides, isDark) { screen = "main" }
            "plans" -> PlansScreen(plans, isDark) { screen = "main" }
            else -> MainScreen(studentName, diaryEntries.size, isDark, { screen = it }, onNavigateBack, { scope.launch { themeManager.toggleTheme() } })
        }
    }
}

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
            item { MenuCard("🤖 Chat IA", Icons.Default.Psychology, "Próximamente", DarkPalette.Primary, DarkPalette.PrimaryVariant) { } }
            item { MenuCard("🫁 Respiración", Icons.Default.Air, "7 ejercicios", DarkPalette.Secondary, DarkPalette.SecondaryVariant) { onNavigate("breathing") } }
            item { MenuCard("📝 Diario", Icons.Default.Book, "$diaryCount entradas", DarkPalette.Tertiary, DarkPalette.TertiaryContainer) { onNavigate("diary") } }
            item { MenuCard("🆘 Ayuda", Icons.Default.Sos, "Líneas emergencia", DarkPalette.Error, DarkPalette.ErrorContainer) { onNavigate("emergency") } }
            item { MenuCard("🎮 Juegos", Icons.Default.Games, "Tus juegos", DarkPalette.MoodHappy, DarkPalette.Warning) { onNavigate("games") } }
            item { MenuCard("📖 Biblioteca", Icons.Default.LibraryBooks, "Guías docentes", DarkPalette.Info, DarkPalette.InfoContainer) { onNavigate("library") } }
            item { MenuCard("📚 Planes", Icons.Default.Assignment, "De docentes", DarkPalette.MoodCalm, DarkPalette.Success) { onNavigate("plans") } }
            item { MenuCard("🚪 Salir", Icons.Default.Logout, "Cerrar sesión", DarkPalette.Warning, DarkPalette.Error) { onLogout() } }
        }
    }
}

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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = exercises.size; for (i in 0 until s) { val ex = exercises[i]; item(key = "ex_$i") { Card(onClick = { selected = ex }, shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = DarkPalette.Primary.copy(alpha = 0.2f)) { Box(contentAlignment = Alignment.Center) { Text("${ex.id}", fontWeight = FontWeight.Bold, color = DarkPalette.Primary) } }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(ex.name, fontWeight = FontWeight.Bold); Text(ex.desc, style = MaterialTheme.typography.bodySmall); Text("${ex.rounds} rondas", style = MaterialTheme.typography.labelSmall, color = DarkPalette.Primary) }; Icon(Icons.Default.PlayArrow, "Iniciar", tint = DarkPalette.Primary) } } } } }
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

@Composable
private fun DiaryScreen(entries: List<DiaryEntry>, isDark: Boolean, onSave: (String, String, String) -> Unit, onDelete: (Int) -> Unit, onBack: () -> Unit) {
    var mood by remember { mutableStateOf("") }; var title by remember { mutableStateOf("") }; var content by remember { mutableStateOf("") }
    val moods = listOf("😊 Feliz" to "HAPPY", "😌 Tranquilo" to "CALM", "😐 Neutral" to "NEUTRAL", "😢 Triste" to "SAD", "😰 Ansioso" to "ANXIOUS", "😠 Enojado" to "ANGRY")
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📝 Diario (${entries.size})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Text("¿Cómo te sientes?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { moods.forEach { (l, c) -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { mood = c }) { Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = if (mood == c) DarkPalette.Primary else DarkPalette.SurfaceVariant) { Box(contentAlignment = Alignment.Center) { Text(l.split(" ").first(), style = MaterialTheme.typography.headlineSmall) } } } } }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Título") }, shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Escribe lo que sientes...") }, shape = RoundedCornerShape(12.dp))
        Button(onClick = { if (mood.isNotEmpty() && content.isNotBlank()) { onSave(mood, title.ifBlank { "Sin título" }, content); mood = ""; title = ""; content = "" } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkPalette.Tertiary)) { Text("💾 Guardar") }
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Diario vacío. Escribe tu primera entrada.", color = DarkPalette.TextMuted) } }
        else { LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) { val s = entries.size; for (i in 0 until s) { val e = entries[i]; item(key = "d_$i") { Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) { Column(modifier = Modifier.padding(16.dp)) { Row { Text(moods.find { it.second == e.mood }?.first?.split(" ")?.first() ?: "😐", style = MaterialTheme.typography.headlineMedium); Spacer(Modifier.width(8.dp)); Column { Text(e.title, fontWeight = FontWeight.Bold); Text(e.date, style = MaterialTheme.typography.labelSmall, color = DarkPalette.TextMuted) } }; Spacer(Modifier.height(8.dp)); Text(e.content) } } } } } }
    }
}

@Composable
private fun EmergencyScreen(isDark: Boolean, onBack: () -> Unit) {
    val contacts = listOf(
        EmerContact(1, "Línea de la Vida", "800-911-2000", "Atención en crisis 24/7 - Gobierno de México", "México"),
        EmerContact(2, "SAPTEL", "55-5259-8121", "Apoyo psicológico gratuito 24/7", "México"),
        EmerContact(3, "Línea Diversa", "55-5658-1111", "Apoyo comunidad LGBTQ+", "México"),
        EmerContact(4, "Línea Nacional contra la Violencia", "800-422-2525", "Violencia de género", "México"),
        EmerContact(5, "LOCATEL", "55-5658-1111", "Atención ciudadana CDMX", "México"),
        EmerContact(6, "Instituto Nacional de Psiquiatría", "55-4160-5000", "Salud mental", "México"),
        EmerContact(7, "Línea 135", "135", "Atención al suicida", "Argentina"),
        EmerContact(8, "Línea 106", "106", "Salud mental", "Colombia"),
        EmerContact(9, "Teléfono Esperanza", "717-003-717", "Apoyo emocional", "España"),
        EmerContact(10, "Línea 024", "024", "Conducta suicida", "España"),
        EmerContact(11, "Salud Responde", "600-360-7777", "Orientación en salud", "Chile"),
        EmerContact(12, "Línea 113", "113", "Salud mental", "Perú"),
        EmerContact(13, "Crisis Text Line", "741741", "Envía HOME al 741741", "Internacional")
    )
    val ctx = LocalContext.current
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🆘 Líneas de Ayuda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.Error.copy(alpha = 0.1f))) { Row(modifier = Modifier.padding(16.dp)) { Icon(Icons.Default.Favorite, null, tint = DarkPalette.Error, modifier = Modifier.size(32.dp)); Spacer(Modifier.width(12.dp)); Text("No estás solo/a. Líneas gratuitas 24/7.", style = MaterialTheme.typography.bodyMedium) } }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contacts.groupBy { it.country }.forEach { (country, list) ->
                item(key = "h_$country") { Text(country, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                val s = list.size; for (i in 0 until s) { val c = list[i]; item(key = "c_${country}_$i") { Card(modifier = Modifier.fillMaxWidth().clickable { try { ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${c.phone}"))) } catch (_: Exception) { } }, shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = DarkPalette.Error.copy(alpha = 0.15f)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Phone, null, tint = DarkPalette.Error) } }; Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(c.name, fontWeight = FontWeight.Bold); Text(c.desc, style = MaterialTheme.typography.bodySmall); Text(c.phone, fontWeight = FontWeight.Bold, color = DarkPalette.Primary, style = MaterialTheme.typography.titleMedium) }; Icon(Icons.Default.Call, "Llamar", tint = DarkPalette.Success) } } } }
            }
        }
    }
}

@Composable
private fun GamesScreen(games: List<String>, isDark: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🎮 Juegos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        if (games.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Games, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay juegos disponibles", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                    Text("El administrador subirá juegos pronto", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = games.size; for (i in 0 until s) { val g = games[i]; item(key = "g_$i") { Card(shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Games, null, tint = DarkPalette.Primary, modifier = Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Text(g, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Icon(Icons.Default.PlayArrow, "Jugar", tint = DarkPalette.Success) } } } } }
        }
    }
}

@Composable
private fun LibraryScreen(guides: List<String>, isDark: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📖 Biblioteca", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        if (guides.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LibraryBooks, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("Biblioteca vacía", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                    Text("Los docentes publicarán guías pronto", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = guides.size; for (i in 0 until s) { val g = guides[i]; item(key = "lib_$i") { Card(shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.MenuBook, null, tint = DarkPalette.Primary, modifier = Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Text(g, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Icon(Icons.Default.Download, "Descargar", tint = DarkPalette.Info) } } } } }
        }
    }
}

@Composable
private fun PlansScreen(plans: List<String>, isDark: Boolean, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("📚 Planes de Estudio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        if (plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Assignment, null, modifier = Modifier.size(64.dp), tint = DarkPalette.TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay planes disponibles", style = MaterialTheme.typography.bodyLarge, color = DarkPalette.TextMuted)
                    Text("Tus docentes publicarán planes de estudio pronto", style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { val s = plans.size; for (i in 0 until s) { val p = plans[i]; item(key = "plan_$i") { Card(shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Assignment, null, tint = DarkPalette.Secondary, modifier = Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Text(p, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)) } } } } }
        }
    }
}

@Composable
private fun MenuCard(title: String, icon: ImageVector, desc: String, c1: Color, c2: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(c1, c2))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, title, modifier = Modifier.size(44.dp), tint = Color.White); Spacer(Modifier.height(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center); Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenSimple(userId: Long, isDark: Boolean, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sanaAI = remember { com.sana.app.core.ai.SanaAI(context) }
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf("Sana" to "¡Hola! 🌟 Soy Sana, tu asistente emocional. ¿Cómo te sientes hoy?")) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("🤖 Sana AI", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPalette.PrimaryContainer))
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val s = messages.size
            for (i in 0 until s) {
                val (role, msg) = messages[i]
                item(key = "m_$i") {
                    val isUser = role == "user"
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                        if (!isUser) { Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = DarkPalette.Primary) { Box(contentAlignment = Alignment.Center) { Text("🤖") } }; Spacer(Modifier.width(8.dp)) }
                        Card(modifier = Modifier.widthIn(max = 260.dp), shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) else RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp), colors = CardDefaults.cardColors(containerColor = if (isUser) DarkPalette.Primary else if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant)) {
                            Text(msg, modifier = Modifier.padding(12.dp), color = if (isUser) Color.White else if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface)
                        }
                    }
                }
            }
            if (isLoading) { item(key = "load") { Row { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)); Text("Escribiendo...") } } }
        }
        Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("Escribe tu mensaje...") }, shape = RoundedCornerShape(24.dp), maxLines = 3)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    if (input.isNotBlank() && !isLoading) {
                        val msg = input.trim()
                        messages = messages + ("user" to msg)
                        input = ""
                        isLoading = true
                        scope.launch {
                            try { messages = messages + ("Sana" to sanaAI.chat(userId.toString(), msg)) }
                            catch (e: Exception) { messages = messages + ("Sana" to "Estoy aquí para ti 💜") }
                            isLoading = false
                        }
                    }
                }, modifier = Modifier.size(48.dp).clip(CircleShape).background(if (input.isNotBlank()) DarkPalette.Primary else DarkPalette.SurfaceVariant), enabled = input.isNotBlank() && !isLoading) { Icon(Icons.Default.Send, "Enviar", tint = Color.White) }
            }
        }
    }
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }
}
