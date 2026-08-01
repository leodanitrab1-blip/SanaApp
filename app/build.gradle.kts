// ============================================
// 🌿 SANA - Build Gradle Módulo App
// Configuración específica de la aplicación Android
// ============================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    // Namespace: Identificador único de la app (reemplaza package en manifest)
    namespace = "com.sana.app"
    
    // Versión de SDK contra la que compilamos
    compileSdk = 34

    defaultConfig {
        // ID único de la aplicación en Google Play y dispositivos
        applicationId = "com.sana.app"
        
        // SDK mínimo: Android 8.0 Oreo (cubre ~95% de dispositivos activos)
        minSdk = 26
        
        // SDK objetivo: Android 14 (último estable al momento)
        targetSdk = 34
        
        // Versión de la app (mayor.menor.parche)
        versionCode = 1
        versionName = "1.0.0"

        // Instrumentación para tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Habilita soporte para vectores animados
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // ============================================
        // CONFIGURACIÓN DE API KEYS Y URLs
        // ============================================
        
        // API Key de Groq para el chat IA
        // ⚠️ CAMBIAR: Obtén tu API key gratuita en https://console.groq.com
        buildConfigField("String", "GROQ_API_KEY", "\"gsk_TU_API_KEY_AQUI\"")
        
        // URL base para GitHub como base de datos
        // ⚠️ CAMBIAR: Reemplaza 'tuusuario' con tu nombre de usuario de GitHub
        buildConfigField("String", "GITHUB_RAW_URL", "\"https://raw.githubusercontent.com/tuusuario/sana-data/main/\"")
        
        // URL de la API de Groq
        buildConfigField("String", "GROQ_BASE_URL", "\"https://api.groq.com/\"")
        
        // Configuración regional por defecto
        resConfigs("es", "en")
    }

    // ============================================
    // TIPOS DE BUILD
    // ============================================
    buildTypes {
        // Build de release (producción)
        release {
            // Habilita minificación y ofuscación
            isMinifyEnabled = true
            // Habilita reducción de recursos (elimina recursos no usados)
            isShrinkResources = true
            // Reglas de ProGuard para ofuscación
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Configuración de firma (se proporciona en CI/CD)
            // signingConfig = signingConfigs.getByName("release")
        }
        
        // Build de debug (desarrollo)
        debug {
            // Sin minificación para depuración más rápida
            isMinifyEnabled = false
            // Habilita logs de debug
            isDebuggable = true
            // ID de aplicación diferente para debug (permite instalar junto a release)
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
        
        // Build staging (pre-producción) opcional
        create("staging") {
            initWith(buildTypes.getByName("debug"))
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    
    // ============================================
    // OPCIONES DE COMPILACIÓN
    // ============================================
    compileOptions {
        // Java 17 para aprovechar últimas características
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        // JVM target consistente con Java
        jvmTarget = "17"
        // Habilita características experimentales de Kotlin
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    
    // ============================================
    // CARACTERÍSTICAS DE BUILD
    // ============================================
    buildFeatures {
        // Habilita Jetpack Compose
        compose = true
        // Habilita generación de BuildConfig
        buildConfig = true
        // Habilita recursos de navegación
        resValues = true
    }
    
    // ============================================
    // CONFIGURACIÓN DE COMPOSE
    // ============================================
    composeOptions {
        // Versión del compilador de Kotlin para Compose
        // Debe ser compatible con la versión de Kotlin del proyecto
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    // ============================================
    // EMPAQUETADO
    // ============================================
    packaging {
        resources {
            // Excluye archivos que causan conflictos en el APK
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Excluye archivos duplicados de META-INF
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE.md"
        }
        // Excluye archivos .so de arquitecturas no soportadas para reducir tamaño
        jniLibs {
            excludes += "**/libjni_others.so"
        }
    }
    
    // ============================================
    // CONFIGURACIÓN DE LINT
    // ============================================
    lint {
        // Habilita chequeos específicos de Compose
        checkDependencies = true
        // Trata errores como warnings en debug
        abortOnError = false
        // Deshabilita chequeos problemáticos
        disable += setOf("MissingTranslation", "ExtraTranslation")
    }
}

// ============================================
// DEPENDENCIAS
// ============================================
dependencies {
    // === 📦 BOM (Bill of Materials) ===
    // BOM de Compose: garantiza compatibilidad entre todas las bibliotecas Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // === 🎨 COMPOSE UI ===
    // UI básica de Compose
    implementation("androidx.compose.ui:ui")
    // Herramientas de gráficos y dibujo
    implementation("androidx.compose.ui:ui-graphics")
    // Vista previa en Android Studio
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Material Design 3 (última versión de Material)
    implementation("androidx.compose.material3:material3")
    // Iconos extendidos de Material (incluye miles de iconos)
    implementation("androidx.compose.material:material-icons-extended")
    // Soporte para ventanas adaptables (tablets, plegables)
    implementation("androidx.compose.material3:material3-window-size-class")
    
    // === 🧭 NAVEGACIÓN ===
    // Navegación con Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // === 🏠 CORE ANDROID ===
    // Extensiones de Kotlin para Android
    implementation("androidx.core:core-ktx:1.12.0")
    // Componentes de ciclo de vida
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // ViewModel con soporte para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // LiveData con soporte para Compose
    implementation("androidx.compose.runtime:runtime-livedata")
    // Activity con Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // === 💾 ROOM DATABASE ===
    // Runtime de Room
    implementation("androidx.room:room-runtime:2.6.1")
    // Extensiones Kotlin para Room (corrutinas, Flow)
    implementation("androidx.room:room-ktx:2.6.1")
    // Procesador de anotaciones para Room (usando KSP)
    ksp("androidx.room:room-compiler:2.6.1")
    
    // === 💉 HILT DI ===
    // Inyección de dependencias con Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    // Procesador de anotaciones para Hilt
    ksp("com.google.dagger:hilt-android-compiler:2.50")
    // Integración de Hilt con Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // === 🌐 NETWORKING ===
    // Retrofit: Cliente HTTP para APIs REST
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Convertidor JSON para Retrofit (usando Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp: Cliente HTTP de bajo nivel
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Interceptor de logs para OkHttp (debug)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // === 📄 JSON ===
    // Gson: Serialización/deserialización JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // === 🖼️ IMÁGENES ===
    // Coil: Carga de imágenes moderna para Compose
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // === 🎬 ANIMACIONES ===
    // Lottie: Animaciones vectoriales (After Effects -> JSON)
    implementation("com.airbnb.android:lottie-compose:6.1.0")
    
    // === 💿 ALMACENAMIENTO ===
    // DataStore: Almacenamiento de preferencias moderno
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // === 📱 SPLASH SCREEN ===
    // Splash Screen API de Android 12+
    implementation("androidx.core:core-splashscreen:1.0.1")
    
    // === 🧪 TESTING ===
    // JUnit para tests unitarios
    testImplementation("junit:junit:4.13.2")
    // Assertions avanzadas
    testImplementation("com.google.truth:truth:1.2.0")
    // Test de corrutinas
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Tests instrumentados
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Test de Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Herramientas de debug (solo en debug)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// ============================================
// TAREAS PERSONALIZADAS
// ============================================

// Tarea para generar documentación de dependencias
tasks.register("dependenciesReport") {
    group = "documentation"
    description = "📊 Genera un reporte de todas las dependencias"
    doLast {
        println("=".repeat(60))
        println("📚 DEPENDENCIAS DEL PROYECTO SANA")
        println("=".repeat(60))
        configurations.forEach { config ->
            if (config.isCanBeResolved) {
                println("\n📦 ${config.name}:")
                config.allDependencies.forEach { dep ->
                    println("  ├── ${dep.group}:${dep.name}:${dep.version}")
                }
            }
        }
    }
}