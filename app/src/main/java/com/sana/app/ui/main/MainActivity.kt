package com.sana.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sana.app.core.theme.SanaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 🌿 SANA - Actividad Principal
 * 
 * Punto de entrada de la aplicación Android.
 * Configura la UI con Jetpack Compose y el tema de Sana.
 * 
 * Características:
 * - Edge-to-edge para pantalla completa inmersiva
 * - Tema adaptativo (oscuro/claro)
 * - Navegación centralizada
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar pantalla completa (barras transparentes)
        enableEdgeToEdge()
        
        setContent {
            SanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SanaApp()
                }
            }
        }
    }
}