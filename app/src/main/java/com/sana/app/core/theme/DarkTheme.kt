package com.sana.app.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DarkPalette {
    val BackgroundGradientStart = Color(0xFF0A0A1A)
    val BackgroundGradientEnd = Color(0xFF1A1A3A)
    val Background = Color(0xFF0A0A1A)
    val Surface = Color(0xFF12122A)
    val SurfaceVariant = Color(0xFF1E1E3A)
    
    val Primary = Color(0xFF6C63FF)
    val PrimaryVariant = Color(0xFF8B83FF)
    val PrimaryContainer = Color(0xFF1A1540)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFFE0DCFF)
    
    val Secondary = Color(0xFF00D4AA)
    val SecondaryVariant = Color(0xFF00E5C0)
    val SecondaryContainer = Color(0xFF00382A)
    val OnSecondary = Color(0xFF00382A)
    val OnSecondaryContainer = Color(0xFF7AF5D0)
    
    val Tertiary = Color(0xFFFFB74D)
    val TertiaryContainer = Color(0xFF3D2A00)
    val OnTertiary = Color(0xFF3D2A00)
    val OnTertiaryContainer = Color(0xFFFFDEA0)
    
    val OnBackground = Color(0xFFE8E8F0)
    val OnSurface = Color(0xFFCCCCDD)
    val OnSurfaceVariant = Color(0xFF9999AA)
    val TextMuted = Color(0xFF666688)
    
    val StarBright = Color(0xFFFFF8DC)
    val StarDim = Color(0x33FFF8DC)
    
    val Error = Color(0xFFFF6B6B)
    val ErrorContainer = Color(0xFF3D1515)
    val Success = Color(0xFF4ECB71)
    val SuccessContainer = Color(0xFF0D3D1A)
    val Warning = Color(0xFFFFD93D)
    val WarningContainer = Color(0xFF3D3500)
    val Info = Color(0xFF5B9BD5)
    val InfoContainer = Color(0xFF0D2D4A)
    
    val Outline = Color(0xFF3A3A5A)
    val CardBorder = Color(0xFF2A2A4A)
    
    val MoodHappy = Color(0xFFFFD93D)
    val MoodCalm = Color(0xFF4ECB71)
    val MoodNeutral = Color(0xFF8899AA)
    val MoodSad = Color(0xFF5B9BD5)
    val MoodAnxious = Color(0xFFFFB74D)
    val MoodAngry = Color(0xFFFF6B6B)
}

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
    background = DarkPalette.Background,
    onBackground = DarkPalette.OnBackground,
    surface = DarkPalette.Surface,
    onSurface = DarkPalette.OnSurface,
    surfaceVariant = DarkPalette.SurfaceVariant,
    onSurfaceVariant = DarkPalette.OnSurfaceVariant,
    error = DarkPalette.Error,
    errorContainer = DarkPalette.ErrorContainer,
    outline = DarkPalette.Outline
)

@Composable
fun SanaDarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}
