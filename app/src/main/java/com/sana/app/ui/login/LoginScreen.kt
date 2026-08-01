package com.sana.app.ui.login

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sana.app.core.theme.DarkPalette
import com.sana.app.core.theme.LightPalette
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.StarryBackground

/**
 * 🌿 SANA - Pantalla de Login
 * 
 * Permite el acceso mediante:
 * - Usuario y contraseña (login tradicional)
 * - Código de acceso (DOC-XXXXXX, ESC-XXXXXX, etc.)
 * - Código maestro de administrador (SANA-ADMIN-2025)
 * 
 * Diseño elegante con:
 * - Campos de texto estilizados
 * - Validación en tiempo real
 * - Indicador de carga durante autenticación
 * - Mensajes de error amigables
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginType: LoginType = LoginType.SCHOOL,
    onLoginSuccess: (Long, String) -> Unit,
    onBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    themeManager: ThemeManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isDark = currentTheme != ThemeManager.THEME_LIGHT

    val focusManager = LocalFocusManager.current

    // Estados locales
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useCodeLogin by remember { mutableStateOf(loginType == LoginType.SCHOOL) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo
        if (isDark) {
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
                starColor = DarkPalette.StarDim,
                starCount = 80
            )
        } else {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // Botón volver
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = if (isDark) DarkPalette.OnBackground
                          else LightPalette.OnBackground
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Título según tipo de login
            Text(
                text = when (loginType) {
                    LoginType.SCHOOL -> "Acceso Escuelas"
                    LoginType.USER -> "Acceso Usuario"
                    LoginType.ADMIN -> "Administrador"
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDark) DarkPalette.OnBackground
                       else LightPalette.OnBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (loginType) {
                    LoginType.SCHOOL -> "Ingresa con tu código de docente, director o alumno"
                    LoginType.USER -> "Accede a tus servicios personalizados"
                    LoginType.ADMIN -> "Código maestro de administración"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) DarkPalette.OnSurfaceVariant
                       else LightPalette.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Formulario de login
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DarkPalette.Surface.copy(alpha = 0.8f)
                                    else LightPalette.Surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    if (useCodeLogin) {
                        // Login por código
                        Text(
                            text = "Código de acceso",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) DarkPalette.OnSurface
                                   else LightPalette.OnSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = accessCode,
                            onValueChange = { code ->
                                accessCode = code.uppercase().take(10)
                                viewModel.clearError()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    when (loginType) {
                                        LoginType.SCHOOL -> "DOC-XXXXXX"
                                        LoginType.ADMIN -> "SANA-ADMIN-2025"
                                        else -> "Código"
                                    }
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Key,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.loginWithCode(accessCode, loginType)
                                }
                            ),
                            isError = uiState.error != null,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Opción de cambiar a login con usuario/contraseña
                        if (loginType != LoginType.ADMIN) {
                            TextButton(
                                onClick = { useCodeLogin = false },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Usar usuario y contraseña")
                            }
                        }

                    } else {
                        // Login con usuario y contraseña
                        Text(
                            text = "Usuario",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) DarkPalette.OnSurface
                                   else LightPalette.OnSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                viewModel.clearError()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Nombre de usuario") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            isError = uiState.error != null,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Contraseña",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) DarkPalette.OnSurface
                                   else LightPalette.OnSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                viewModel.clearError()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contraseña") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                                     else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Ocultar"
                                                           else "Mostrar"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                                                  else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.loginWithCredentials(username, password)
                                }
                            ),
                            isError = uiState.error != null,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Opción de cambiar a login con código
                        TextButton(
                            onClick = { useCodeLogin = true },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Usar código de acceso")
                        }
                    }

                    // Mensaje de error
                    AnimatedVisibility(
                        visible = uiState.error != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) DarkPalette.ErrorContainer
                                                else LightPalette.ErrorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isDark) DarkPalette.Error
                                          else LightPalette.Error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.error ?: "",
                                    color = if (isDark) DarkPalette.Error
                                           else LightPalette.Error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de login
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (useCodeLogin) {
                                viewModel.loginWithCode(accessCode, loginType)
                            } else {
                                viewModel.loginWithCredentials(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        enabled = !uiState.isLoading && (if (useCodeLogin) accessCode.isNotBlank()
                                                         else username.isNotBlank() && password.isNotBlank()),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) DarkPalette.Primary
                                            else LightPalette.Primary
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = androidx.compose.ui.graphics.Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Ingresar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Efecto de éxito
        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess && uiState.userId != null) {
                onLoginSuccess(uiState.userId!!, uiState.sessionToken ?: "")
            }
        }
    }
}

/**
 * Tipos de login disponibles
 */
enum class LoginType {
    SCHOOL,  // Login para escuelas (código)
    USER,    // Login para usuarios (servicios)
    ADMIN    // Login de administrador
}