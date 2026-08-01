package com.sana.app.ui.login

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun LoginScreen(loginType: LoginType = LoginType.SCHOOL, onLoginSuccess: (Long, String) -> Unit, onBack: () -> Unit, viewModel: LoginViewModel = hiltViewModel(), themeManager: ThemeManager) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState(initial = ThemeManager.THEME_DARK)
    val isDark = currentTheme != ThemeManager.THEME_LIGHT
    val focusManager = LocalFocusManager.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useCodeLogin by remember { mutableStateOf(loginType == LoginType.SCHOOL) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkPalette.BackgroundGradientStart, DarkPalette.BackgroundGradientEnd)))); StarryBackground(starColor = DarkPalette.StarDim, starCount = 80) }
        else { Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightPalette.BackgroundGradientStart, LightPalette.BackgroundGradientEnd)))) }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) { Icon(Icons.Default.ArrowBack, "Volver", tint = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground) }
            Spacer(Modifier.weight(0.5f))
            Text(when (loginType) { LoginType.SCHOOL -> "Acceso Escuelas"; LoginType.USER -> "Acceso Usuario"; LoginType.ADMIN -> "Administrador" }, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(48.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f) else LightPalette.Surface.copy(alpha = 0.9f)), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    if (useCodeLogin) {
                        OutlinedTextField(value = accessCode, onValueChange = { accessCode = it.uppercase().take(10); viewModel.clearError() }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("DOC-XXXXXX") }, leadingIcon = { Icon(Icons.Default.Key, null) }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), isError = uiState.error != null, shape = RoundedCornerShape(12.dp))
                        if (loginType != LoginType.ADMIN) { TextButton(onClick = { useCodeLogin = false }) { Text("Usar usuario y contraseña") } }
                    } else {
                        OutlinedTextField(value = username, onValueChange = { username = it; viewModel.clearError() }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Usuario") }, leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), isError = uiState.error != null, shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = password, onValueChange = { password = it; viewModel.clearError() }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Contraseña") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, if (passwordVisible) "Ocultar" else "Mostrar") } }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), isError = uiState.error != null, shape = RoundedCornerShape(12.dp))
                        TextButton(onClick = { useCodeLogin = true }) { Text("Usar código de acceso") }
                    }

                    AnimatedVisibility(visible = uiState.error != null, enter = fadeIn(), exit = fadeOut()) {
                        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) DarkPalette.ErrorContainer else LightPalette.ErrorContainer), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Error, null, tint = DarkPalette.Error); Spacer(Modifier.width(12.dp)); Text(uiState.error ?: "", color = DarkPalette.Error) }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { focusManager.clearFocus(); if (useCodeLogin) viewModel.loginWithCode(accessCode, loginType) else viewModel.loginWithCredentials(username, password) }, modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)), enabled = !uiState.isLoading && (if (useCodeLogin) accessCode.isNotBlank() else username.isNotBlank() && password.isNotBlank()), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary)) {
                        if (uiState.isLoading) { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp) }
                        else { Icon(Icons.Default.Login, null); Spacer(Modifier.width(8.dp)); Text("Ingresar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }
        LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess && uiState.userId != null) onLoginSuccess(uiState.userId!!, uiState.sessionToken ?: "") }
    }
}
