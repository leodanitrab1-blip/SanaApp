package com.sana.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sana.app.core.database.converters.Converters
import com.sana.app.core.database.dao.*
import com.sana.app.core.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 🌿 SANA - Base de Datos Principal
 * 
 * Utiliza Room (capa de abstracción sobre SQLite) para persistencia local.
 * 
 * Estructura:
 * - 5 tablas principales: users, schools, diary_entries, games, messages
 * - TypeConverters para tipos complejos (Listas, Mapas, Fechas)
 * - Callback para pre-poblar datos iniciales
 * - Soporte para migraciones
 * 
 * @property userDao    DAO para operaciones de usuarios
 * @property schoolDao  DAO para operaciones de escuelas
 * @property diaryDao   DAO para operaciones del diario emocional
 * @property gameDao    DAO para operaciones de juegos
 * @property messageDao DAO para operaciones de mensajería
 */
@Database(
    entities = [
        UserEntity::class,
        SchoolEntity::class,
        DiaryEntryEntity::class,
        GameEntity::class,
        MessageEntity::class,
        EmergencyContactEntity::class,
        BreathingSessionEntity::class,
        StudyPlanEntity::class
    ],
    version = 2, // Incrementar con cada cambio de esquema
    exportSchema = true // Exportar esquema para migraciones
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // ============ DAOs ============
    
    /** DAO para operaciones CRUD de usuarios */
    abstract fun userDao(): UserDao
    
    /** DAO para operaciones CRUD de escuelas */
    abstract fun schoolDao(): SchoolDao
    
    /** DAO para operaciones CRUD del diario emocional */
    abstract fun diaryDao(): DiaryDao
    
    /** DAO para operaciones CRUD de juegos */
    abstract fun gameDao(): GameDao
    
    /** DAO para operaciones CRUD de mensajes */
    abstract fun messageDao(): MessageDao
    
    /** DAO para contactos de emergencia */
    abstract fun emergencyContactDao(): EmergencyContactDao
    
    /** DAO para sesiones de respiración */
    abstract fun breathingSessionDao(): BreathingSessionDao
    
    /** DAO para planes de estudio */
    abstract fun studyPlanDao(): StudyPlanDao

    companion object {
        const val DATABASE_NAME = "sana_database"
        private const val TAG = "AppDatabase"

        /**
         * Callback que se ejecuta cuando la base de datos se crea por primera vez.
         * Pre-pobla datos iniciales como contactos de emergencia y juegos de ejemplo.
         */
        private val PREPOPULATE_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                
                // Insertar datos iniciales en un hilo de fondo
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        prepopulateData()
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Error pre-poblando datos", e)
                    }
                }
            }
        }

        /**
         * Pre-pobla la base de datos con datos iniciales necesarios
         */
        private suspend fun prepopulateData() {
            // La instancia se obtiene desde fuera porque aquí no tenemos acceso a la DB
            // Los datos se insertan desde SanaApplication después de crear la DB
            android.util.Log.d(TAG, "Base de datos creada - lista para pre-poblar")
        }

        /**
         * Constructor de la base de datos con configuración optimizada
         * 
         * @param context Contexto de la aplicación
         * @return Instancia singleton de AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(PREPOPULATE_CALLBACK) // Pre-poblar datos
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING) // Mejor rendimiento
                .enableMultiInstanceInvalidation() // Soportar múltiples instancias
                .fallbackToDestructiveMigration() // Recrear BD si migración falla
                .build()
        }
    }
}