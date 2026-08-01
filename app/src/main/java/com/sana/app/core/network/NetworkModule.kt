package com.sana.app.core.network

import com.sana.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * 🌿 SANA - Módulo de Red (Hilt)
 * 
 * Provee las dependencias relacionadas con networking:
 * - OkHttpClient: Cliente HTTP configurado con caché y timeouts
 * - Retrofit (GitHub): Para datos remotos (JSON en repositorio)
 * - Retrofit (Groq): Para el chat de IA
 * - ApiService: Interfaz para GitHub
 * - GroqApiService: Interfaz para Groq
 * 
 * Configuraciones:
 * - Timeout de 30 segundos para conexiones lentas
 * - Caché HTTP de 10MB para reducir uso de datos
 * - Logging detallado en debug
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ============================================
    // CONSTANTES DE CONFIGURACIÓN
    // ============================================
    
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB

    // ============================================
    // OKHTTP CLIENT (COMPARTIDO)
    // ============================================

    /**
     * Provee un OkHttpClient configurado con:
     * - Caché en disco
     * - Timeouts razonables
     * - Logging de requests/responses en debug
     * - Headers comunes
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("cacheDir") cacheDir: File
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Caché HTTP
            .cache(Cache(
                directory = File(cacheDir, "http_cache"),
                maxSize = CACHE_SIZE
            ))
            // Timeouts
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            // Reintentos automáticos
            .retryOnConnectionFailure(true)
            // Seguir redirecciones
            .followRedirects(true)
            .followSslRedirects(true)
            // Interceptor de logging (solo en debug)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            // Interceptor para headers comunes
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "Sana-App-Android/${BuildConfig.VERSION_NAME}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // ============================================
    // DIRECTORIO DE CACHÉ
    // ============================================

    @Provides
    @Singleton
    @Named("cacheDir")
    fun provideCacheDir(): File {
        // Usar cacheDir de la app (se limpia cuando el sistema necesita espacio)
        return File(System.getProperty("java.io.tmpdir") ?: "/tmp")
    }

    // ============================================
    // RETROFIT - GITHUB (BASE DE DATOS REMOTA)
    // ============================================

    /**
     * Retrofit configurado para GitHub Raw
     * Base URL: https://raw.githubusercontent.com/tuusuario/sana-data/main/
     */
    @Provides
    @Singleton
    @Named("github")
    fun provideGithubRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_RAW_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Interfaz ApiService para comunicación con GitHub
     */
    @Provides
    @Singleton
    fun provideApiService(@Named("github") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // ============================================
    // RETROFIT - GROQ (CHAT IA)
    // ============================================

    /**
     * Retrofit configurado para la API de Groq
     * Base URL: https://api.groq.com/
     */
    @Provides
    @Singleton
    @Named("groq")
    fun provideGroqRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GROQ_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Interfaz GroqApiService para el chat de IA
     */
    @Provides
    @Singleton
    fun provideGroqApiService(@Named("groq") retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }

    // ============================================
    // GROQ API KEY
    // ============================================

    /**
     * Provee la API key de Groq como String formateado
     * para el header de autorización
     */
    @Provides
    @Singleton
    @Named("groqAuth")
    fun provideGroqAuthHeader(): String {
        return "Bearer ${BuildConfig.GROQ_API_KEY}"
    }
}