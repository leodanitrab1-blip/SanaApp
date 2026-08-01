package com.sana.app.ui.login

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.R
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Pantalla de Selección de Rol
 * 
 * Pantalla inicial con tres botones principales:
 * - Escuelas: Acceso para docentes, directores y alumnos
 * - Usuario: Acceso a servicios (chat IA, diario, juegos, etc.)
 * - Administrador: Acceso al panel de administración
 * 
 * Diseño visualmente impactante con:
 * - Fondo de estrellas animadas (tema oscuro)
 * - Fondo natural tranquilo (tema claro)
 * - Botones con gradientes y animaciones
 * - Logo personalizado de Sana centrado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onNavigateToSchoolLogin: () -> Unit,
    onNavigateToUserServices: () -> Unit,
    onNavigateToAdminLogin: () -> Unit,
    themeManager: ThemeManager = hiltViewModel()
) {
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo según el tema
        if (isDark) {
            // Fondo oscuro con estrellas
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
            StarryBackground(
                starColor = DarkPalette.StarBright,
                starCount = 150
            )
        } else {
            // Fondo claro natural
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

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Espacio superior
            Spacer(modifier = Modifier.weight(1f))

            // Logo personalizado de Sana
            SanaLogo(isDark = isDark)

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "Tu espacio seguro de bienestar emocional",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDark) DarkPalette.OnSurface
                       else LightPalette.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones de selección de rol
            RoleButton(
                text = "Escuelas",
                subtitle = "Docentes, Directores, Alumnos",
                icon = Icons.Default.School,
                gradientColors = if (isDark)
                    listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant)
                else
                    listOf(LightPalette.Primary, LightPalette.PrimaryVariant),
                onClick = onNavigateToSchoolLogin
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleButton(
                text = "Usuario",
                subtitle = "Chat IA, Diario, Juegos, Ayuda",
                icon = Icons.Default.Person,
                gradientColors = if (isDark)
                    listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant)
                else
                    listOf(LightPalette.Secondary, LightPalette.SecondaryVariant),
                onClick = onNavigateToUserServices
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleButton(
                text = "Administrador",
                subtitle = "Gestión de escuelas y contenido",
                icon = Icons.Default.AdminPanelSettings,
                gradientColors = if (isDark)
                    listOf(DarkPalette.Tertiary, DarkPalette.TertiaryContainer)
                else
                    listOf(LightPalette.Tertiary, LightPalette.TertiaryContainer),
                onClick = onNavigateToAdminLogin
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cambio de tema
            ThemeToggleButton(
                isDark = isDark,
                onToggle = { themeManager.toggleTheme() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Versión
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) DarkPalette.TextMuted
                       else LightPalette.TextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Logo de Sana personalizado
 * Usa la imagen logo_personal.png desde drawable
 */
@Composable
private fun SanaLogo(isDark: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo personalizado cargado desde recursos drawable
        Image(
            painter = painterResource(id = R.drawable.logo_personal),
            contentDescription = "Sana Logo",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de la app debajo del logo
        Text(
            text = "Sana",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                letterSpacing = 4.sp
            ),
            color = if (isDark) DarkPalette.OnBackground
                   else LightPalette.OnBackground
        )
    }
}

/**
 * Botón de selección de rol con gradiente y animación
 */
@Composable
private fun RoleButton(
    text: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(colors = gradientColors)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(32.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ir",
                    modifier = Modifier.size(24.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

/**
 * Botón para alternar entre tema oscuro y claro
 */
@Composable
private fun ThemeToggleButton(
    isDark: Boolean,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Icon(
            imageVector = if (isDark) Icons.Default.LightMode
                         else Icons.Default.DarkMode,
            contentDescription = if (isDark) "Cambiar a tema claro"
                                else "Cambiar a tema oscuro",
            tint = if (isDark) DarkPalette.StarBright
                  else LightPalette.EarthBrown,
            modifier = Modifier.size(28.dp)
        )
    }
}