// ============================================
// 🌿 SANA - Build Gradle (Raíz del Proyecto)
// Configuración principal de plugins y repositorios
// ============================================

// Definición de plugins disponibles para todos los módulos
// Las versiones se manejan centralizadamente para evitar conflictos
plugins {
    // Plugin de Android - Gestiona la compilación de apps Android
    id("com.android.application") version "8.2.2" apply false
    
    // Plugin de Kotlin para Android - Soporte del lenguaje Kotlin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    
    // Plugin de Kotlin Symbol Processing (KSP) - Alternativa más rápida a kapt
    // Necesario para Room y otras bibliotecas que usan procesamiento de anotaciones
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    
    // Hilt - Inyección de dependencias para Android
    // Facilita la gestión de dependencias y mejora la testeabilidad
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

// Configuración global de compilación
allprojects {
    // Ya no se usa buildscript, los plugins se declaran arriba
}

// Tarea para limpiar el directorio de build
// Útil cuando hay problemas de caché o builds corruptos
tasks.register("clean", Delete::class) {
    description = "🧹 Limpia todos los directorios de build del proyecto"
    group = "build"
    delete(rootProject.layout.buildDirectory)
}