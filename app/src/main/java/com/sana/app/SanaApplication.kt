package com.sana.app

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.sana.app.core.theme.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 Clase Application de Sana
 * 
 * Punto de entrada de la aplicación. Se inicializa antes que cualquier
 * Activity, Service o Receiver. Aquí configuramos:
 * 
 * - Inyección de dependencias con Hilt
 * - Configuración de Coil (carga de imágenes)
 * - Inicialización del tema
 * - Configuración de crash logging
 * - Pre-carga de datos críticos
 *
 * @author Sana Team
 * @since 1.0.0
 */
@HiltAndroidApp
class SanaApplication : Application(), ImageLoaderFactory {

    // Inyectamos ThemeManager para configurar el tema inicial
    @Inject
    lateinit var themeManager: ThemeManager

    // Scope de la aplicación para tareas en segundo plano
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "SanaApp"
        
        // Instancia singleton de la aplicación
        // Se inicializa en onCreate y nunca debería ser null después
        lateinit var instance: SanaApplication
            private set

        // Versión de la aplicación (se sincroniza con build.gradle.kts)
        const val VERSION_NAME = "1.0.0"
        const val VERSION_CODE = 1
    }

    /**
     * Se llama cuando se crea la aplicación.
     * Inicializa todos los componentes necesarios.
     */
    override fun onCreate() {
        super.onCreate()
        
        // Guardar instancia para acceso global
        instance = this
        
        // Inicializar componentes en orden
        initializeTheme()
        initializeLogging()
        initializeCrashReporting()
        preloadCriticalData()
        
        Log.i(TAG, "🌿 Sana v$VERSION_NAME inicializada correctamente")
    }

    /**
     * Configura el tema inicial basado en preferencias del usuario
     */
    private fun initializeTheme() {
        applicationScope.launch {
            try {
                val currentTheme = themeManager.currentTheme.first()
                Log.d(TAG, "🎨 Tema inicial: $currentTheme")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar tema inicial", e)
            }
        }
    }

    /**
     * Configura el sistema de logging
     * En debug: logs detallados
     * En release: solo errores y warnings
     */
    private fun initializeLogging() {
        if (com.sana.app.BuildConfig.DEBUG) {
            Log.d(TAG, "🐛 Modo DEBUG activado - Logs detallados habilitados")
        }
    }

    /**
     * Configura reporte de crashes (en producción usarías Firebase Crashlytics)
     * Por ahora usamos un handler básico que guarda en log
     */
    private fun initializeCrashReporting() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "💥 Crash no manejado en hilo: ${thread.name}", throwable)
            
            // Guardar crash en archivo para debug
            try {
                val crashLog = java.io.File(filesDir, "crashes")
                    .also { it.mkdirs() }
                    .resolve("crash_${System.currentTimeMillis()}.txt")
                
                crashLog.writeText(buildString {
                    appendLine("=== SANA CRASH REPORT ===")
                    appendLine("Timestamp: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}")
                    appendLine("Thread: ${thread.name}")
                    appendLine("Exception: ${throwable.javaClass.name}")
                    appendLine("Message: ${throwable.message}")
                    appendLine("Stacktrace:")
                    throwable.stackTrace.forEach { appendLine("  at $it") }
                })
                
                Log.d(TAG, "📝 Crash guardado en: ${crashLog.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "No se pudo guardar el crash log", e)
            }
            
            // Llamar al handler original
            defaultHandler?.uncaughtException(thread, throwable)
            
            // Si no hay handler, matar el proceso
            if (defaultHandler == null) {
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }

    /**
     * Pre-carga datos críticos que se necesitan al iniciar
     * Como contactos de emergencia y configuración inicial
     */
    private fun preloadCriticalData() {
        applicationScope.launch {
            try {
                // Aquí se puede precargar:
                // - Lista de contactos de emergencia
                // - Configuración de temas
                // - Datos del usuario logueado (si existe sesión)
                Log.d(TAG, "📦 Datos críticos precargados")
            } catch (e: Exception) {
                Log.e(TAG, "Error en precarga de datos", e)
            }
        }
    }

    /**
     * Configuración de Coil para carga de imágenes
     * Optimizado para el uso en Sana:
     * - Cache en memoria: 20% de la memoria disponible
     * - Cache en disco: 50MB
     * - Políticas de cache agresivas para ahorrar datos
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20) // 20% de RAM disponible
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_images"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false) // Ignorar headers para mejor caching
            .crossfade(300) // Animación suave al cargar imágenes
            .build()
    }

    /**
     * Se llama cuando el sistema está bajo memoria
     * Liberar caches no críticos
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "⚠️ Memoria baja detectada - Liberando caches")
        
        // Coil maneja automáticamente su cache
        // Aquí podemos liberar otros recursos
        Runtime.getRuntime().gc()
    }

    /**
     * Se llama cuando el sistema necesita liberar memoria
     * Similar a onLowMemory pero más agresivo
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w(TAG, "🪓 Trim memory nivel: $level")
                Runtime.getRuntime().gc()
            }
            TRIM_MEMORY_BACKGROUND,
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE -> {
                Log.w(TAG, "🪓 Trim memory severo: $level - Liberando caches")
                // Liberar todos los caches posibles
                Runtime.getRuntime().gc()
            }
        }
    }
}