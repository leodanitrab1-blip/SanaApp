package com.sana.app.core.utils

/**
 * 🌿 SANA - Constantes Globales
 * 
 * Valores constantes utilizados en toda la aplicación.
 * Centralizados aquí para facilitar cambios y mantenimiento.
 */
object Constants {

    // ============================================
    // CÓDIGOS DE ACCESO Y PREFIJOS
    // ============================================

    /** Código maestro del administrador principal */
    const val ADMIN_MASTER_CODE = "SANA-ADMIN-2025"

    /** Prefijo para códigos de escuela */
    const val SCHOOL_PREFIX = "ESC"

    /** Prefijo para códigos de docente */
    const val TEACHER_PREFIX = "DOC"

    /** Prefijo para códigos de director */
    const val DIRECTOR_PREFIX = "ADM"

    /** Prefijo para códigos de padre de familia */
    const val PARENT_PREFIX = "PAD"

    /** Prefijo para códigos de estudiante */
    const val STUDENT_PREFIX = "STU"

    // ============================================
    // ROLES DE USUARIO
    // ============================================

    const val ROLE_STUDENT = "STUDENT"
    const val ROLE_TEACHER = "TEACHER"
    const val ROLE_DIRECTOR = "DIRECTOR"
    const val ROLE_ADMIN = "ADMIN"
    const val ROLE_PARENT = "PARENT"

    /** Lista de todos los roles válidos */
    val ALL_ROLES = listOf(ROLE_STUDENT, ROLE_TEACHER, ROLE_DIRECTOR, ROLE_ADMIN, ROLE_PARENT)

    // ============================================
    // ESTADOS DE ÁNIMO (DIARIO EMOCIONAL)
    // ============================================

    /** Estados de ánimo con emoji y descripción */
    val MOODS = listOf(
        "HAPPY" to "😊 Feliz",
        "CALM" to "😌 Tranquilo",
        "NEUTRAL" to "😐 Neutral",
        "SAD" to "😢 Triste",
        "ANXIOUS" to "😰 Ansioso",
        "ANGRY" to "😠 Enojado"
    )

    /** Solo los códigos de estados de ánimo */
    val MOOD_CODES = MOODS.map { it.first }

    // ============================================
    // CATEGORÍAS DE JUEGOS
    // ============================================

    const val GAME_CATEGORY_GENERAL = "GENERAL"
    const val GAME_CATEGORY_PRIMARY = "PRIMARY"
    const val GAME_CATEGORY_SECONDARY = "SECONDARY"

    val GAME_CATEGORIES = listOf(GAME_CATEGORY_GENERAL, GAME_CATEGORY_PRIMARY, GAME_CATEGORY_SECONDARY)

    // ============================================
    // TIPOS DE MENSAJE
    // ============================================

    const val MESSAGE_TYPE_MESSAGE = "MESSAGE"
    const val MESSAGE_TYPE_GUIDE = "GUIDE"
    const val MESSAGE_TYPE_PLAN = "PLAN"
    const val MESSAGE_TYPE_ANNOUNCEMENT = "ANNOUNCEMENT"

    // ============================================
    // VISIBILIDAD DE PLANES DE ESTUDIO
    // ============================================

    const val VISIBILITY_PRIVATE = "PRIVATE"
    const val VISIBILITY_SCHOOL = "SCHOOL"
    const val VISIBILITY_PUBLIC = "PUBLIC"

    // ============================================
    // NIVELES EDUCATIVOS
    // ============================================

    const val LEVEL_PRIMARY = "PRIMARY"
    const val LEVEL_SECONDARY = "SECONDARY"
    const val LEVEL_HIGH_SCHOOL = "HIGH_SCHOOL"
    const val LEVEL_MIXED = "MIXED"

    // ============================================
    // URLs Y CONFIGURACIÓN DE RED
    // ============================================

    const val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
    const val GITHUB_REPO = "https://github.com/tuusuario/sana-data"

    /** Timeouts en segundos */
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // ============================================
    // CONFIGURACIÓN DE CACHÉ
    // ============================================

    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE_HOURS = 24

    // ============================================
    // EJERCICIOS DE RESPIRACIÓN
    // ============================================

    /** Lista de ejercicios de respiración predefinidos */
    val BREATHING_EXERCISES = listOf(
        BreathingExercise(
            id = 1,
            name = "Relajación 4-7-8",
            description = "Inhala 4s, retén 7s, exhala 8s",
            inhaleSeconds = 4,
            holdSeconds = 7,
            exhaleSeconds = 8,
            rounds = 4
        ),
        BreathingExercise(
            id = 2,
            name = "Respiración Cuadrada",
            description = "Inhala, retén, exhala, retén - todos 4s",
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 4,
            rounds = 5
        ),
        BreathingExercise(
            id = 3,
            name = "Calma Rápida",
            description = "Inhala 3s, exhala 6s",
            inhaleSeconds = 3,
            holdSeconds = 0,
            exhaleSeconds = 6,
            rounds = 5
        ),
        BreathingExercise(
            id = 4,
            name = "Energía Matutina",
            description = "Inhala 6s, exhala 2s - energizante",
            inhaleSeconds = 6,
            holdSeconds = 0,
            exhaleSeconds = 2,
            rounds = 8
        ),
        BreathingExercise(
            id = 5,
            name = "Anti-Ansiedad",
            description = "Inhala 5s, retén 5s, exhala 10s",
            inhaleSeconds = 5,
            holdSeconds = 5,
            exhaleSeconds = 10,
            rounds = 3
        ),
        BreathingExercise(
            id = 6,
            name = "Sueño Profundo",
            description = "Inhala 4s, retén 7s, exhala 8s - x10 rondas",
            inhaleSeconds = 4,
            holdSeconds = 7,
            exhaleSeconds = 8,
            rounds = 10
        ),
        BreathingExercise(
            id = 7,
            name = "Respiración Consciente",
            description = "Respira libremente con guía visual",
            inhaleSeconds = 5,
            holdSeconds = 0,
            exhaleSeconds = 5,
            rounds = 3
        )
    )

    // ============================================
    // PAÍSES PARA CONTACTOS DE EMERGENCIA
    // ============================================

    val SUPPORTED_COUNTRIES = listOf(
        "México", "Argentina", "Colombia", "España",
        "Chile", "Perú", "Uruguay", "Internacional"
    )

    // ============================================
    // CONFIGURACIÓN DE UI
    // ============================================

    const val ANIMATION_DURATION_MS = 300
    const val SNACKBAR_DURATION_SHORT = 2000L
    const val SNACKBAR_DURATION_LONG = 4000L

    /** Número máximo de estrellas en el fondo oscuro */
    const val MAX_STARS = 300
    const val MIN_STARS = 50
    const val DEFAULT_STARS = 150
}

/**
 * Modelo de datos para ejercicios de respiración
 */
data class BreathingExercise(
    val id: Int,
    val name: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdSeconds: Int,
    val exhaleSeconds: Int,
    val rounds: Int
)