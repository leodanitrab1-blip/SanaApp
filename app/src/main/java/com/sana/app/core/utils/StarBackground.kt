package com.sana.app.core.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * 🌿 SANA - Fondo de Estrellas Animadas
 * 
 * Crea un hermoso fondo con estrellas que titilan suavemente.
 * Usado en el tema oscuro para crear una atmósfera relajante.
 * 
 * Características:
 * - Estrellas de diferentes tamaños y brillos
 * - Animación de titileo suave
 * - Distribución aleatoria pero consistente
 * - Configurable (cantidad, color, velocidad)
 */

/**
 * Representa una estrella individual en el fondo
 */
data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val baseAlpha: Float,
    val twinkleSpeed: Float,
    val twinkleOffset: Float
)

/**
 * Fondo de estrellas con animación de titileo
 * 
 * @param modifier Modificador para el Canvas
 * @param starColor Color base de las estrellas
 * @param starCount Cantidad de estrellas (50-300)
 * @param minRadius Radio mínimo de estrellas
 * @param maxRadius Radio máximo de estrellas
 */
@Composable
fun StarryBackground(
    modifier: Modifier = Modifier,
    starColor: Color = Color(0xFFFFF8DC),
    starCount: Int = Constants.DEFAULT_STARS,
    minRadius: Float = 0.5f,
    maxRadius: Float = 3.5f
) {
    // Generar estrellas una sola vez y recordarlas
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * (maxRadius - minRadius) + minRadius,
                baseAlpha = Random.nextFloat() * 0.6f + 0.2f,
                twinkleSpeed = Random.nextFloat() * 0.03f + 0.005f,
                twinkleOffset = Random.nextFloat() * Math.PI.toFloat() * 2
            )
        }
    }

    // Animación infinita para el titileo
    val infiniteTransition = rememberInfiniteTransition(label = "starTwinkle")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Float.MAX_VALUE,
        animationSpec = infiniteRepeatable(
            animation = TweenSpec(durationMillis = Int.MAX_VALUE),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            // Calcular alpha con efecto de titileo sinusoidal
            val twinkle = (Math.sin((time * star.twinkleSpeed + star.twinkleOffset).toDouble()) * 0.3 + 0.7).toFloat()
            val alpha = (star.baseAlpha * twinkle).coerceIn(0f, 1f)

            // Dibujar estrella con brillo (círculo exterior más grande y tenue)
            if (star.radius > 2f) {
                drawCircle(
                    color = starColor.copy(alpha = alpha * 0.3f),
                    radius = star.radius * 3f,
                    center = Offset(star.x * size.width, star.y * size.height)
                )
            }

            // Dibujar estrella principal
            drawCircle(
                color = starColor.copy(alpha = alpha),
                radius = star.radius,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

/**
 * Versión simplificada del fondo estrellado sin animación
 * Útil para pantallas de bajo rendimiento
 */
@Composable
fun StaticStarryBackground(
    modifier: Modifier = Modifier,
    starColor: Color = Color(0xFFFFF8DC),
    starCount: Int = 100
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2f + 0.5f,
                baseAlpha = Random.nextFloat() * 0.5f + 0.3f,
                twinkleSpeed = 0f,
                twinkleOffset = 0f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            drawCircle(
                color = starColor.copy(alpha = star.baseAlpha),
                radius = star.radius,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}