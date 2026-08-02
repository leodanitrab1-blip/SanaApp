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
    var showRegister by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.userId != null && uiState.userRole != null) {
            onLoginSuccess(uiState.userId!!, uiState.userRole!!)
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
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver", tint = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground) }
            }
            Spacer(Modifier.weight(1f))

            Text(
                text = when (loginType) { LoginType.ADMIN -> "👑 Administrador"; LoginType.SCHOOL -> "🏫 Escuelas"; LoginType.USER -> "👤 Usuario" },
                style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold,
                color = if (isDark) DarkPalette.OnBackground else LightPalette.OnBackground
            )
            Spacer(Modifier.height(8.dp))

            if (showRegister && loginType == LoginType.USER) {
                // REGISTRO DE ALUMNO
                Text("Crear cuenta de alumno", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(value = studentName, onValueChange = { studentName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tu nombre completo") }, shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (studentName.isNotBlank()) {
                        generatedCode = viewModel.registerStudent(studentName)
                    }
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Crear cuenta") }
                
                if (generatedCode.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DarkPalette.SuccessContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("✅ ¡Cuenta creada!", fontWeight = FontWeight.Bold)
                            Text("Tu código es:")
                            Text(generatedCode, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("\n⚠️ ¡Guarda este código! Lo necesitarás para entrar.")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { showRegister = false; accessCode = generatedCode }) { Text("Usar este código para entrar") }
                }
            } else {
                // LOGIN NORMAL
                Text(
                    text = when (loginType) { LoginType.ADMIN -> "Código maestro"; LoginType.SCHOOL -> "Código de acceso"; LoginType.USER -> "Tu código personal" },
                    style = MaterialTheme.typography.bodyLarge, color = if (isDark) DarkPalette.OnSurfaceVariant else LightPalette.OnSurfaceVariant
                )
                Spacer(Modifier.height(40.dp))

                OutlinedTextField(
                    value = accessCode, onValueChange = { accessCode = it.uppercase(); viewModel.clearError() },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(when (loginType) { LoginType.ADMIN -> "SANA-ADMIN-2025"; LoginType.SCHOOL -> "DOC-XXXXXX"; LoginType.USER -> "STU-XXXXXX" }) },
                    leadingIcon = { Icon(Icons.Default.Key, null) }, singleLine = true, isError = uiState.error != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), shape = RoundedCornerShape(16.dp)
                )

                if (uiState.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = DarkPalette.ErrorContainer), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(12.dp)) { Icon(Icons.Default.Error, null, tint = DarkPalette.Error, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(uiState.error!!, color = DarkPalette.Error) }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(onClick = { focusManager.clearFocus(); viewModel.tryLogin(accessCode.trim(), loginType) },
                    modifier = Modifier.fillMaxWidth().height(60.dp), enabled = accessCode.isNotBlank() && !uiState.isLoading,
                    shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isDark) DarkPalette.Primary else LightPalette.Primary)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = androidx.compose.ui.graphics.Color.White)
                    else { Icon(Icons.Default.Login, null, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(8.dp)); Text("Ingresar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                }

                // Opción de registro para alumnos
                if (loginType == LoginType.USER) {
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = { showRegister = true }) { Text("¿No tienes cuenta? Regístrate aquí") }
                }
            }
            Spacer(Modifier.weight(2f))
        }
    }
}
