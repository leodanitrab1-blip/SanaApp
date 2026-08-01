package com.sana.app.ui.login

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
import com.sana.app.R
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onNavigateToSchoolLogin: () -> Unit,
    onNavigateToUserServices: () -> Unit,
    onNavigateToAdminLogin: () -> Unit,
    themeManager: ThemeManager
) {
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd))))
            StarryBackground(starColor = DarkPalette.StarBright, starCount = 150)
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd))))
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.weight(1f))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.logo_personal), contentDescription = "Sana Logo", modifier = Modifier.size(120.dp).clip(RoundedCornerShape(24.dp)), contentScale = ContentScale.Fit)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sana", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 48.sp, letterSpacing = 4.sp), color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tu espacio seguro de bienestar emocional", style = MaterialTheme.typography.bodyLarge, color = if (isDark) DarkPalette.OnSurface else LightPalette.OnSurface, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1f))

            // Botones
            RoleButton("Escuelas", "Docentes, Directores, Alumnos", Icons.Default.School, listOf(DarkPalette.Primary, DarkPalette.PrimaryVariant), onClick = onNavigateToSchoolLogin)
            Spacer(modifier = Modifier.height(16.dp))
            RoleButton("Usuario", "Chat IA, Diario, Juegos, Ayuda", Icons.Default.Person, listOf(DarkPalette.Secondary, DarkPalette.SecondaryVariant), onClick = onNavigateToUserServices)
            Spacer(modifier = Modifier.height(16.dp))
            RoleButton("Administrador", "Gestión de escuelas y contenido", Icons.Default.AdminPanelSettings, listOf(DarkPalette.Tertiary, DarkPalette.Tertiary), onClick = onNavigateToAdminLogin)

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { scope.launch { themeManager.toggleTheme() } }, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp))) {
                Icon(imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Tema", tint = if (isDark) DarkPalette.StarBright else LightPalette.EarthBrown, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "v1.0.0", style = MaterialTheme.typography.labelSmall, color = if (isDark) DarkPalette.TextMuted else LightPalette.TextMuted)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RoleButton(text: String, subtitle: String, icon: ImageVector, gradientColors: List<androidx.compose.ui.graphics.Color>, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent), contentPadding = PaddingValues(0.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colors = gradientColors)), contentAlignment = Alignment.Center) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(32.dp), tint = androidx.compose.ui.graphics.Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text(text = text, style = MaterialTheme.typography.titleLarge, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold); Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)) }
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Ir", modifier = Modifier.size(24.dp), tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    }
}
