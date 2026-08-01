package com.sana.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

enum class LoginType { SCHOOL, USER, ADMIN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginType: LoginType = LoginType.SCHOOL,
    onLoginSuccess: (Long, String) -> Unit,
    onBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    themeManager: ThemeManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val focusManager = LocalFocusManager.current
    var accessCode by remember { mutableStateOf("") }

    // Navegar al dashboard cuando login exitoso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.userId != null) {
            onLoginSuccess(uiState.userId!!, "session")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd))))
            StarryBackground(starColor = DarkPalette.StarDim, starCount = 60)
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd))))
        }

        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Botón volver
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Volver", tint = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground)
                }
            }

            Spacer(Modifier.weight(1f))

            // Título
            Text(
                text = when (loginType) {
                    LoginType.ADMIN -> "👑 Administrador"
                    LoginType.SCHOOL -> "🏫 Escuelas"
                    LoginType.USER -> "👤 Usuario"
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ingresa tu código de acceso",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            // Campo de código
            OutlinedTextField(
                value = accessCode,
                onValueChange = { 
                    accessCode = it.uppercase()
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        when (loginType) {
                            LoginType.ADMIN -> "SANA-ADMIN-2025"
                            LoginType.SCHOOL -> "DOC-XXXXXX o ESC-XXXXXX"
                            LoginType.USER -> "Tu código de acceso"
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Key, null) },
                singleLine = true,
                isError = uiState.error != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDark) DarkPalette.Primary else LightPalette.Primary,
                    unfocusedBorderColor = if (isDark) DarkPalette.Outline else LightPalette.Outline,
                    cursorColor = if (isDark) DarkPalette.Primary else LightPalette.Primary
                )
            )

            // Mensaje de error
            if (uiState.error != null) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkPalette.ErrorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, null, tint = DarkPalette.Error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(uiState.error!!, color = DarkPalette.Error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón Ingresar
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.tryLogin(accessCode.trim(), loginType)
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = accessCode.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary
                )
            ) {
                Icon(Icons.Default.Login, null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Ingresar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.weight(2f))
        }
    }
}
