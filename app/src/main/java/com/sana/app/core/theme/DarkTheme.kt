package com.sana.app.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * 🌿 SANA - Tema Oscuro
 * 
 * Paleta de colores oscuros con estrellas:
 * - Fondo: Degradado azul muy oscuro (#0A0A1A → #1A1A3A)
 * - Acentos: Púrpura vibrante y verde menta
 * - Estrellas: Dorado suave en el fondo
 * 
 * Diseñado para ser relajante y reducir fatiga visual,
 * ideal para uso nocturno y diario emocional.
 */

// ============================================
// PALETA DE COLORES OSCUROS
// ============================================
object DarkPalette {
    // Fondos - Degradado de azul profundo
    val BackgroundGradientStart = Color(0xFF0A0A1A)   // Azul casi negro
    val BackgroundGradientEnd = Color(0xFF1A1A3A)     // Azul medianoche
    val Background = Color(0xFF0A0A1A)
    val Surface = Color(0xFF12122A)                   // Superficie de tarjetas
    val SurfaceVariant = Color(0xFF1E1E3A)            // Variante más clara
    val SurfaceBright = Color(0xFF2A2A4A)             // Superficie brillante

    // Acentos principales
    val Primary = Color(0xFF6C63FF)                   // Púrpura vibrante
    val PrimaryContainer = Color(0xFF1A1540)          // Contenedor púrpura oscuro
    val OnPrimary = Color(0xFFFFFFFF)                 // Texto sobre primario
    val OnPrimaryContainer = Color(0xFFE0DCFF)        // Texto sobre contenedor
    
    val Secondary = Color(0xFF00D4AA)                 // Verde menta
    val SecondaryContainer = Color(0xFF00382A)        // Contenedor verde oscuro
    val OnSecondary = Color(0xFF00382A)               // Texto sobre secundario
    val OnSecondaryContainer = Color(0xFF7AF5D0)      // Texto sobre contenedor

    val Tertiary = Color(0xFFFFB74D)                  // Naranja/dorado suave
    val TertiaryContainer = Color(0xFF3D2A00)         // Contenedor naranja oscuro
    val OnTertiary = Color(0xFF3D2A00)
    val OnTertiaryContainer = Color(0xFFFFDEA0)

    // Texto
    val OnBackground = Color(0xFFE8E8F0)              // Blanco hueso para texto principal
    val OnSurface = Color(0xFFCCCCDD)                 // Gris claro para texto secundario
    val OnSurfaceVariant = Color(0xFF9999AA)          // Gris medio para texto terciario
    val TextMuted = Color(0xFF666688)                 // Texto atenuado

    // Estrellas (fondo)
    val StarBright = Color(0xFFFFF8DC)                // Dorado brillante
    val StarMedium = Color(0x88FFF8DC)                // Dorado medio
    val StarDim = Color(0x33FFF8DC)                   // Dorado tenue
    val StarTwinkle = Color(0xFFFFE4B5)               // Mocasín para brillo

    // Estados y semántica
    val Error = Color(0xFFFF6B6B)                     // Rojo suave
    val ErrorContainer = Color(0xFF3D1515)            // Contenedor error
    val OnError = Color(0xFF3D1515)
    val OnErrorContainer = Color(0xFFFFB4B4)
    
    val Success = Color(0xFF4ECB71)                   // Verde éxito
    val SuccessContainer = Color(0xFF0D3D1A)          // Contenedor éxito
    
    val Warning = Color(0xFFFFD93D)                   // Amarillo advertencia
    val WarningContainer = Color(0xFF3D3500)          // Contenedor advertencia
    
    val Info = Color(0xFF5B9BD5)                      // Azul informativo
    val InfoContainer = Color(0xFF0D2D4A)             // Contenedor info

    // Elementos de UI
    val CardBorder = Color(0xFF2A2A4A)                // Borde de tarjetas
    val Divider = Color(0xFF1E1E3A)                   // Divisores
    val Ripple = Color(0x33FFFFFF)                    // Efecto ripple
    val Scrim = Color(0x99000000)                     // Scrim para modales
    val Outline = Color(0xFF3A3A5A)                   // Contornos

    // Estados de ánimo (colores para el diario emocional)
    val MoodHappy = Color(0xFFFFD93D)                 // Amarillo feliz
    val MoodCalm = Color(0xFF4ECB71)                  // Verde calma
    val MoodNeutral = Color(0xFF8899AA)               // Gris neutral
    val MoodSad = Color(0xFF5B9BD5)                   // Azul triste
    val MoodAnxious = Color(0xFFFFB74D)               // Naranja ansioso
    val MoodAngry = Color(0xFFFF6B6B)                 // Rojo enojado

    // Gradiente de fondo para pantallas principales
    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
    )
}

/**
 * Tema oscuro de Material 3 para Sana
 */
private fun darkColorScheme() = darkColorScheme(
    primary = DarkPalette.Primary,
    onPrimary = DarkPalette.OnPrimary,
    primaryContainer = DarkPalette.PrimaryContainer,
    onPrimaryContainer = DarkPalette.OnPrimaryContainer,
    secondary = DarkPalette.Secondary,
    onSecondary = DarkPalette.OnSecondary,
    secondaryContainer = DarkPalette.SecondaryContainer,
    onSecondaryContainer = DarkPalette.OnSecondaryContainer,
    tertiary = DarkPalette.Tertiary,
    onTertiary = DarkPalette.OnTertiary,
    tertiaryContainer = DarkPalette.TertiaryContainer,
    onTertiaryContainer = DarkPalette.OnTertiaryContainer,
    background = DarkPalette.Background,
    onBackground = DarkPalette.OnBackground,
    surface = DarkPalette.Surface,
    onSurface = DarkPalette.OnSurface,
    surfaceVariant = DarkPalette.SurfaceVariant,
    onSurfaceVariant = DarkPalette.OnSurfaceVariant,
    error = DarkPalette.Error,
    errorContainer = DarkPalette.ErrorContainer,
    onError = DarkPalette.OnError,
    onErrorContainer = DarkPalette.OnErrorContainer,
    outline = DarkPalette.Outline,
    scrim = DarkPalette.Scrim
)

@Composable
fun SanaDarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = SanaTypography,
        shapes = SanaShapes,
        content = content
    )
}