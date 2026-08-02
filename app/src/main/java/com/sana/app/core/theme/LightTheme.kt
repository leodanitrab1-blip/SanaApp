package com.sana.app.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object LightPalette {
    val BackgroundGradientStart = Color(0xFFF5F0E8)
    val BackgroundGradientEnd = Color(0xFFEDE5D8)
    val Background = Color(0xFFF5F0E8)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF0EBE0)
    
    val Primary = Color(0xFF4A7C59)
    val PrimaryVariant = Color(0xFF6B9B7A)
    val PrimaryContainer = Color(0xFFD4E8D8)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFF1A3A24)
    
    val Secondary = Color(0xFF8B6914)
    val SecondaryVariant = Color(0xFFA6842B)
    val SecondaryContainer = Color(0xFFF5E6C8)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF3D2A00)
    
    val Tertiary = Color(0xFF6B8E6B)
    val TertiaryContainer = Color(0xFFD4E8D8)
    val OnTertiary = Color(0xFFFFFFFF)
    val OnTertiaryContainer = Color(0xFF1A3A24)
    
    val OnBackground = Color(0xFF2C2416)
    val OnSurface = Color(0xFF4A3F2F)
    val OnSurfaceVariant = Color(0xFF6B5F4F)
    val TextMuted = Color(0xFF8B806F)
    
    val EarthBrown = Color(0xFF8B7355)
    
    val Error = Color(0xFFC44545)
    val ErrorContainer = Color(0xFFF5D5D5)
    val Success = Color(0xFF4A8C4A)
    val SuccessContainer = Color(0xFFD5F5D5)
    val Warning = Color(0xFFD4A843)
    val WarningContainer = Color(0xFFF5F0D5)
    val Info = Color(0xFF5B8BA5)
    val InfoContainer = Color(0xFFD5EAF5)
    
    val Outline = Color(0xFFC8C0B0)
    val CardBorder = Color(0xFFE0D8C8)
}

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
    background = LightPalette.Background,
    onBackground = LightPalette.OnBackground,
    surface = LightPalette.Surface,
    onSurface = LightPalette.OnSurface,
    surfaceVariant = LightPalette.SurfaceVariant,
    onSurfaceVariant = LightPalette.OnSurfaceVariant,
    error = LightPalette.Error,
    errorContainer = LightPalette.ErrorContainer,
    outline = LightPalette.Outline
)

@Composable
fun SanaLightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}
