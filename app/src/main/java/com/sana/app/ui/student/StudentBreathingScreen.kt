package com.sana.app.ui.student

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.utils.BreathingExercise
import com.sana.app.core.utils.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentBreathingScreen(isDark: Boolean, onBack: () -> Unit) {
    var selectedExercise by remember { mutableStateOf<BreathingExercise?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("ready") }
    var secondsLeft by remember { mutableStateOf(3) }
    var currentRound by remember { mutableStateOf(1) }
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

    if (selectedExercise == null) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }; Text("🫁 Respiración Guiada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Constants.BREATHING_EXERCISES) { exercise ->
                    Card(onClick = { selectedExercise = exercise }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else DarkPalette.Surface)) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = DarkPalette.Primary.copy(alpha = 0.2f)) { Box(contentAlignment = Alignment.Center) { Text("${exercise.id}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DarkPalette.Primary) } }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) { Text(exercise.name, fontWeight = FontWeight.Bold); Text(exercise.description, style = MaterialTheme.typography.bodySmall, color = DarkPalette.TextMuted); Text("${exercise.rounds} rondas", style = MaterialTheme.typography.labelSmall, color = DarkPalette.Primary) }
                            Icon(Icons.Default.PlayArrow, "Iniciar", tint = DarkPalette.Primary)
                        }
                    }
                }
            }
        }
    } else {
        val exercise = selectedExercise!!
        val scaleAnimation by animateFloatAsState(targetValue = when (currentPhase) { "inhale" -> 1.5f; "exhale" -> 0.6f; else -> 1f }, animationSpec = tween(1000, easing = EaseInOutCubic), label = "s")
        val phaseColor by animateColorAsState(targetValue = when (currentPhase) { "inhale" -> DarkPalette.Primary; "hold" -> DarkPalette.Tertiary; "exhale" -> DarkPalette.Secondary; else -> DarkPalette.OnSurface }, animationSpec = tween(500), label = "c")

        LaunchedEffect(isPlaying, currentRound) {
            if (isPlaying) {
                for (round in currentRound..exercise.rounds) { currentRound = round
                    currentPhase = "inhale"; secondsLeft = exercise.inhaleSeconds; for (s in exercise.inhaleSeconds downTo 1) { delay(1000); secondsLeft = s }
                    if (exercise.holdSeconds > 0) { currentPhase = "hold"; secondsLeft = exercise.holdSeconds; for (s in exercise.holdSeconds downTo 1) { delay(1000); secondsLeft = s } }
                    currentPhase = "exhale"; secondsLeft = exercise.exhaleSeconds; for (s in exercise.exhaleSeconds downTo 1) { delay(1000); secondsLeft = s }
                }
                currentPhase = "completed"; isPlaying = false
                try { mediaPlayer = MediaPlayer.create(context, android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)); mediaPlayer?.start() } catch (_: Exception) { }
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(exercise.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = if (isDark) DarkPalette.OnBackground else DarkPalette.OnBackground)
            Spacer(Modifier.height(40.dp))
            Box(modifier = Modifier.size(220.dp).scale(scaleAnimation), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = phaseColor.copy(alpha = 0.2f), radius = size.minDimension / 2, style = Fill)
                    drawCircle(color = phaseColor, radius = size.minDimension / 2, style = Stroke(width = 6.dp.toPx()))
                    val angle = (System.currentTimeMillis() % 5000) / 5000f * 360f
                    val px = size.width/2 + (size.minDimension / 2.5f) * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                    val py = size.height/2 + (size.minDimension / 2.5f) * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                    drawCircle(color = phaseColor.copy(alpha = 0.6f), radius = 8.dp.toPx(), center = Offset(px, py))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(when (currentPhase) { "inhale" -> "🌬️ Inhala"; "hold" -> "🫁 Retén"; "exhale" -> "💨 Exhala"; "completed" -> "✅ Completo"; else -> "✨ Listo" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = phaseColor)
                    Text("$secondsLeft", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = phaseColor, fontSize = 56.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
            Text("Ronda $currentRound de ${exercise.rounds}", style = MaterialTheme.typography.titleMedium, color = if (isDark) DarkPalette.OnSurface else DarkPalette.OnSurface)
            LinearProgressIndicator(progress = currentRound.toFloat() / exercise.rounds, modifier = Modifier.fillMaxWidth(0.6f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = phaseColor, trackColor = if (isDark) DarkPalette.SurfaceVariant else DarkPalette.SurfaceVariant)
            Spacer(Modifier.height(48.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                OutlinedButton(onClick = { selectedExercise = null; isPlaying = false }, shape = CircleShape, modifier = Modifier.size(64.dp)) { Icon(Icons.Default.Close, "Salir") }
                Button(onClick = { if (currentPhase == "completed") { currentRound = 1; currentPhase = "ready"; secondsLeft = 3 }; isPlaying = !isPlaying }, shape = CircleShape, modifier = Modifier.size(80.dp), colors = ButtonDefaults.buttonColors(containerColor = phaseColor)) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (isPlaying) "Pausar" else "Iniciar", modifier = Modifier.size(40.dp), tint = Color.White) }
                Spacer(Modifier.size(64.dp))
            }
        }
    }
}
