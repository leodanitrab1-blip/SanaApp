package com.sana.app.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * 🌿 SANA - Tema Claro
 * 
 * Paleta de colores claros con motivos naturales:
 * - Fondo: Blanco hueso (#F5F0E8) con textura suave
 * - Acentos: Verde bosque y marrón dorado
 * - Elementos naturales: hojas, cielo, tierra
 * 
 * Diseñado para transmitir calma, naturaleza y bienestar,
 * ideal para uso diurno y lectura prolongada.
 */

// ============================================
// PALETA DE COLORES CLAROS
// ============================================
object LightPalette {
    // Fondos - Tonos naturales
    val BackgroundGradientStart = Color(0xFFF5F0E8)   // Blanco hueso
    val BackgroundGradientEnd = Color(0xFFEDE5D8)     // Crema suave
    val Background = Color(0xFFF5F0E8)                // Blanco hueso
    val Surface = Color(0xFFFFFFFF)                    // Blanco puro
    val SurfaceVariant = Color(0xFFF0EBE0)             // Crema
    val SurfaceBright = Color(0xFFFAF6F0)              // Casi blanco

    // Acentos naturales
    val Primary = Color(0xFF4A7C59)                    // Verde bosque
    val PrimaryContainer = Color(0xFFD4E8D8)           // Verde pálido
    val OnPrimary = Color(0xFFFFFFFF)                  // Blanco sobre primario
    val OnPrimaryContainer = Color(0xFF1A3A24)         // Verde oscuro
    
    val Secondary = Color(0xFF8B6914)                  // Marrón dorado
    val SecondaryContainer = Color(0xFFF5E6C8)         // Crema dorado
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF3D2A00)

    val Tertiary = Color(0xFF6B8E6B)                   // Verde salvia
    val TertiaryContainer = Color(0xFFD4E8D8)          // Verde pálido
    val OnTertiary = Color(0xFFFFFFFF)
    val OnTertiaryContainer = Color(0xFF1A3A24)

    // Texto
    val OnBackground = Color(0xFF2C2416)               // Marrón oscuro
    val OnSurface = Color(0xFF4A3F2F)                  // Marrón medio
    val OnSurfaceVariant = Color(0xFF6B5F4F)           // Marrón claro
    val TextMuted = Color(0xFF8B806F)                  // Texto atenuado

    // Elementos naturales
    val LeafGreen = Color(0xFF6B8E6B)                  // Verde hoja
    val LeafLight = Color(0xFFA8D8A8)                  // Verde hoja clara
    val SkyBlue = Color(0xFF87CEEB)                    // Azul cielo
    val SkyLight = Color(0xFFB8E4F5)                   // Azul cielo claro
    val EarthBrown = Color(0xFF8B7355)                 // Marrón tierra
    val EarthLight = Color(0xFFC4A882)                 // Marrón tierra claro
    val FlowerPink = Color(0xFFE8B4C4)                 // Rosa flor
    val SunYellow = Color(0xFFF5E6A8)                  // Amarillo sol

    // Estados y semántica
    val Error = Color(0xFFC44545)                      // Rojo suave
    val ErrorContainer = Color(0xFFF5D5D5)             // Contenedor error
    val OnError = Color(0xFFFFFFFF)
    val OnErrorContainer = Color(0xFF3D1515)
    
    val Success = Color(0xFF4A8C4A)                    // Verde éxito
    val SuccessContainer = Color(0xFFD5F5D5)           // Contenedor éxito
    
    val Warning = Color(0xFFD4A843)                    // Ámbar advertencia
    val WarningContainer = Color(0xFFF5F0D5)           // Contenedor advertencia
    
    val Info = Color(0xFF5B8BA5)                       // Azul informativo
    val InfoContainer = Color(0xFFD5EAF5)              // Contenedor info

    // Elementos de UI
    val CardBorder = Color(0xFFE0D8C8)                 // Borde de tarjetas
    val Divider = Color(0xFFE8E0D0)                    // Divisores
    val Ripple = Color(0x33000000)                     // Efecto ripple
    val Scrim = Color(0x99000000)                      // Scrim
    val Outline = Color(0xFFC8C0B0)                    // Contornos

    // Estados de ánimo
    val MoodHappy = Color(0xFFF5D54A)                  // Amarillo feliz
    val MoodCalm = Color(0xFF6BBF6B)                   // Verde calma
    val MoodNeutral = Color(0xFFA0A8A0)                // Gris neutral
    val MoodSad = Color(0xFF6BA0C8)                    // Azul triste
    val MoodAnxious = Color(0xFFF0B84A)                // Naranja ansioso
    val MoodAngry = Color(0xFFE86868)                   // Rojo enojado

    // Gradiente de fondo natural
    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
    )
}

/**
 * Tema claro de Material 3 para Sana
 */
private fun lightColorScheme() = lightColorScheme(
    primary = LightPalette.Primary,
    onPrimary = LightPalette.OnPrimary,
    primaryContainer = LightPalette.PrimaryContainer,
    onPrimaryContainer = LightPalette.OnPrimaryContainer,
    secondary = LightPalette.Secondary,
    onSecondary = LightPalette.OnSecondary,
    secondaryContainer = LightPalette.SecondaryContainer,
    onSecondaryContainer = LightPalette.OnSecondaryContainer,
    tertiary = LightPalette.Tertiary,
    onTertiary = LightPalette.OnTertiary,
    tertiaryContainer = LightPalette.TertiaryContainer,
    onTertiaryContainer = LightPalette.OnTertiaryContainer,
    background = LightPalette.Background,
    onBackground = LightPalette.OnBackground,
    surface = LightPalette.Surface,
    onSurface = LightPalette.OnSurface,
    surfaceVariant = LightPalette.SurfaceVariant,
    onSurfaceVariant = LightPalette.OnSurfaceVariant,
    error = LightPalette.Error,
    errorContainer = LightPalette.ErrorContainer,
    onError = LightPalette.OnError,
    onErrorContainer = LightPalette.OnErrorContainer,
    outline = LightPalette.Outline,
    scrim = LightPalette.Scrim
)

@Composable
fun SanaLightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = SanaTypography,
        shapes = SanaShapes,
        content = content
    )
}