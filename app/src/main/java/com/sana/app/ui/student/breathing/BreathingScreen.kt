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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
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
import kotlinx.coroutines.delay

/**
 * 🌿 SANA - Ejercicios de Respiración Guiada
 * 
 * 7 ejercicios de respiración con animación circular y guía visual.
 * Características:
 * - Animación de expansión/contracción
 * - Indicador de fase (inhala, retén, exhala)
 * - Contador de rondas
 * - Vibración háptica en transiciones
 * - Sonidos de respiración (si están disponibles)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    viewModel: BreathingViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

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
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 60)
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
                    Text("🫁 Respiración Guiada", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                )
            )

            if (uiState.selectedExercise == null) {
                // Lista de ejercicios
                ExerciseList(
                    exercises = Constants.BREATHING_EXERCISES,
                    onSelect = { viewModel.selectExercise(it) },
                    isDark = isDark
                )
            } else {
                // Ejercicio activo
                ActiveExercise(
                    exercise = uiState.selectedExercise!!,
                    phase = uiState.currentPhase,
                    secondsLeft = uiState.secondsLeft,
                    currentRound = uiState.currentRound,
                    totalRounds = uiState.totalRounds,
                    isPlaying = uiState.isPlaying,
                    onStart = { viewModel.startExercise() },
                    onPause = { viewModel.pauseExercise() },
                    onStop = { viewModel.stopExercise() },
                    isDark = isDark
                )
            }
        }
    }
}

/**
 * Lista de ejercicios disponibles
 */
@Composable
private fun ExerciseList(
    exercises: List<BreathingExercise>,
    onSelect: (BreathingExercise) -> Unit,
    isDark: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Elige un ejercicio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "7 ejercicios para diferentes momentos del día",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(exercises) { exercise ->
            Card(
                onClick = { onSelect(exercise) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DarkPalette.Surface
                                    else LightPalette.Surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Círculo con número
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (isDark) DarkPalette.Primary.copy(alpha = 0.2f)
                               else LightPalette.Primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${exercise.id}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkPalette.Primary
                                       else LightPalette.Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkPalette.OnSurface
                                   else LightPalette.OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) DarkPalette.OnSurfaceVariant
                                   else LightPalette.OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${exercise.rounds} rondas • ${exercise.inhaleSeconds}s / ${exercise.holdSeconds}s / ${exercise.exhaleSeconds}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) DarkPalette.TextMuted
                                   else LightPalette.TextMuted
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (isDark) DarkPalette.TextMuted
                              else LightPalette.TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Pantalla de ejercicio activo con animación
 */
@Composable
private fun ActiveExercise(
    exercise: BreathingExercise,
    phase: String,
    secondsLeft: Int,
    currentRound: Int,
    totalRounds: Int,
    isPlaying: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    isDark: Boolean
) {
    // Animación de escala para el círculo de respiración
    val scaleAnimation by animateFloatAsState(
        targetValue = when (phase) {
            "inhale" -> 1.4f
            "hold" -> 1.4f
            "exhale" -> 0.6f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = when (phase) {
                "inhale" -> exercise.inhaleSeconds * 1000
                "exhale" -> exercise.exhaleSeconds * 1000
                else -> 500
            },
            easing = LinearEasing
        ),
        label = "breathingScale"
    )

    // Color según la fase
    val phaseColor by animateColorAsState(
        targetValue = when (phase) {
            "inhale" -> if (isDark) DarkPalette.Primary else LightPalette.Primary
            "hold" -> if (isDark) DarkPalette.Tertiary else LightPalette.Tertiary
            "exhale" -> if (isDark) DarkPalette.Secondary else LightPalette.Secondary
            else -> if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
        },
        animationSpec = tween(500),
        label = "phaseColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Nombre del ejercicio
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Círculo de respiración animado
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = phaseColor.copy(alpha = 0.3f),
                    radius = size.minDimension / 2,
                    style = Fill
                )
                drawCircle(
                    color = phaseColor,
                    radius = size.minDimension / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 4.dp.toPx()
                    )
                )
            }

            // Texto central
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when (phase) {
                        "inhale" -> "Inhala"
                        "hold" -> "Retén"
                        "exhale" -> "Exhala"
                        "ready" -> "Preparado"
                        "rest" -> "Descansa"
                        else -> ""
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = phaseColor
                )
                Text(
                    text = "$secondsLeft",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = phaseColor,
                    fontSize = 48.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progreso de rondas
        Text(
            text = "Ronda $currentRound de $totalRounds",
            style = MaterialTheme.typography.titleMedium,
            color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { currentRound.toFloat() / totalRounds.toFloat() },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = phaseColor,
            trackColor = if (isDark) DarkPalette.SurfaceVariant
                        else LightPalette.SurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botones de control
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Botón Parar
            OutlinedButton(
                onClick = onStop,
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDark) DarkPalette.Error else LightPalette.Error
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Parar")
            }

            // Botón Iniciar/Pausar
            Button(
                onClick = if (isPlaying) onPause else onStart,
                shape = CircleShape,
                modifier = Modifier.size(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = phaseColor
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Iniciar",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Espacio para balance visual
            Spacer(modifier = Modifier.size(64.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instrucción adicional
        Text(
            text = when (phase) {
                "inhale" -> "Inhala lentamente por la nariz"
                "hold" -> "Mantén el aire suavemente"
                "exhale" -> "Exhala despacio por la boca"
                "ready" -> "Prepárate para comenzar..."
                "rest" -> "Respira normalmente"
                else -> ""
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}