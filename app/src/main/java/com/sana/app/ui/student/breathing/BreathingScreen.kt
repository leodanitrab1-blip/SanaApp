package com.sana.app.ui.student.breathing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.BreathingExercise
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.StarryBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(userId: Long, onNavigateBack: () -> Unit, viewModel: BreathingViewModel = hiltViewModel(), themeManager: ThemeManager) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 60) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("🫁 Respiración Guiada", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)))

            if (uiState.selectedExercise == null) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(Constants.BREATHING_EXERCISES) { exercise ->
                        Card(onClick = { viewModel.selectExercise(exercise) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface else LightPalette.Surface)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = if (isDark) DarkPalette.Primary.copy(alpha = 0.2f) else LightPalette.Primary.copy(alpha = 0.1f)) { Box(contentAlignment = Alignment.Center) { Text("${exercise.id}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (isDark) DarkPalette.Primary else LightPalette.Primary) } }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) { Text(exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(exercise.description, style = MaterialTheme.typography.bodySmall); Text("${exercise.rounds} rondas", style = MaterialTheme.typography.labelSmall) }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            } else {
                val exercise = uiState.selectedExercise!!
                val scaleAnimation by animateFloatAsState(targetValue = when (uiState.currentPhase) { "inhale" -> 1.4f; "exhale" -> 0.6f; else -> 1f }, animationSpec = tween(1000, easing = LinearEasing), label = "scale")
                val phaseColor by animateColorAsState(targetValue = when (uiState.currentPhase) { "inhale" -> if (isDark) DarkPalette.Primary else LightPalette.Primary; "hold" -> if (isDark) DarkPalette.Tertiary else LightPalette.Tertiary; "exhale" -> if (isDark) DarkPalette.Secondary else LightPalette.Secondary; else -> if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface }, animationSpec = tween(500), label = "color")

                Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(exercise.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier.size(200.dp).scale(scaleAnimation), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(color = phaseColor.copy(alpha = 0.3f), radius = size.minDimension / 2, style = Fill); drawCircle(color = phaseColor, radius = size.minDimension / 2, style = Stroke(width = 4.dp.toPx())) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(when (uiState.currentPhase) { "inhale" -> "Inhala"; "hold" -> "Retén"; "exhale" -> "Exhala"; else -> "Listo" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = phaseColor); Text("${uiState.secondsLeft}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = phaseColor, fontSize = 48.sp) }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Ronda ${uiState.currentRound} de ${uiState.totalRounds}", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(progress = { uiState.currentRound.toFloat() / uiState.totalRounds.toFloat() }, modifier = Modifier.fillMaxWidth(0.6f).height(8.dp), color = phaseColor, trackColor = if (isDark) DarkPalette.SurfaceVariant else LightPalette.SurfaceVariant)
                    Spacer(modifier = Modifier.height(48.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        OutlinedButton(onClick = { viewModel.stopExercise() }, shape = CircleShape, modifier = Modifier.size(64.dp)) { Icon(Icons.Default.Stop, "Parar") }
                        Button(onClick = { if (uiState.isPlaying) viewModel.pauseExercise() else viewModel.startExercise() }, shape = CircleShape, modifier = Modifier.size(80.dp), colors = ButtonDefaults.buttonColors(containerColor = phaseColor)) { Icon(if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (uiState.isPlaying) "Pausar" else "Iniciar", modifier = Modifier.size(36.dp)) }
                        Spacer(modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
    }
}
