// ============================================
// 🌿 SANA - Settings Gradle
// Configuración de módulos y repositorios de dependencias
// ============================================

// Gestión de plugins - Centraliza las versiones y repositorios de plugins
pluginManagement {
    repositories {
        // Repositorio principal de Google para plugins Android
        google {
            content {
                // Limita Google a solo plugins oficiales de Android/Kotlin
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        
        // Repositorio central de Maven
        mavenCentral()
        
        // Repositorio de Gradle para plugins oficiales
        gradlePluginPortal()
    }
    
    // Configuración de versiones de plugins
    plugins {
        // Las versiones se heredan del build.gradle.kts raíz
    }
}

// Gestión de dependencias - Controla dónde se buscan las bibliotecas
dependencyResolutionManagement {
    // FAIL_ON_PROJECT_REPOS evita que módulos individuales definan sus propios repositorios
    // Esto centraliza la gestión de repositorios y mejora la seguridad
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        // Repositorio de Google para bibliotecas AndroidX, Material, etc.
        google()
        
        // Repositorio central de Maven para bibliotecas generales
        mavenCentral()
        
        // Repositorio JitPack para bibliotecas desde GitHub
        // Ejemplo: algunas bibliotecas de gráficos o utilidades
        maven { url = uri("https://jitpack.io") }
    }
}

// Definición del proyecto raíz
rootProject.name = "Sana"

// Inclusión del módulo app (único módulo por ahora)
// Si más adelante necesitas módulos separados (ej: core, features), se agregan aquí
include(":app")

// Configuración de build cache
buildCache {
    local {
        // Habilita cache local para builds más rápidas
        isEnabled = true
        // Directorio de cache dentro del proyecto (puede ser externo también)
        directory = uri(rootDir.resolve(".gradle/build-cache"))
        // Tiempo de vida del cache: 7 días
        removeUnusedEntriesAfterDays = 7
    }
}