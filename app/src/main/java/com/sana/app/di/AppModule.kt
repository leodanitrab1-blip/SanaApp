package com.sana.app.di

import android.content.Context
import androidx.room.Room
import com.sana.app.core.database.AppDatabase
import com.sana.app.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 🌿 SANA - Módulo de Inyección de Dependencias (Hilt)
 * 
 * Provee las dependencias principales de la aplicación:
 * - Base de datos Room y DAOs
 * - Repositorios (se proveen desde sus propios módulos o constructor)
 * 
 * Hilt se encarga de:
 * - Crear singletons automáticamente
 * - Inyectar dependencias en Activities, ViewModels, etc.
 * - Gestionar el ciclo de vida de los componentes
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ============================================
    // BASE DE DATOS
    // ============================================

    /**
     * Provee la instancia singleton de la base de datos Room
     * 
     * Configuración:
     * - Nombre: sana_database
     * - Modo WAL para mejor rendimiento concurrente
     * - Migración destructiva como fallback
     * 
     * @param context Contexto de la aplicación
     * @return Instancia de AppDatabase
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .setJournalMode(androidx.room.RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    // ============================================
    // DAOs (Data Access Objects)
    // ============================================

    /**
     * Provee el DAO de usuarios
     */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    /**
     * Provee el DAO de escuelas
     */
    @Provides
    @Singleton
    fun provideSchoolDao(database: AppDatabase): SchoolDao {
        return database.schoolDao()
    }

    /**
     * Provee el DAO del diario emocional
     */
    @Provides
    @Singleton
    fun provideDiaryDao(database: AppDatabase): DiaryDao {
        return database.diaryDao()
    }

    /**
     * Provee el DAO de juegos
     */
    @Provides
    @Singleton
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }

    /**
     * Provee el DAO de mensajes
     */
    @Provides
    @Singleton
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    /**
     * Provee el DAO de contactos de emergencia
     */
    @Provides
    @Singleton
    fun provideEmergencyContactDao(database: AppDatabase): EmergencyContactDao {
        return database.emergencyContactDao()
    }

    /**
     * Provee el DAO de sesiones de respiración
     */
    @Provides
    @Singleton
    fun provideBreathingSessionDao(database: AppDatabase): BreathingSessionDao {
        return database.breathingSessionDao()
    }

    /**
     * Provee el DAO de planes de estudio
     */
    @Provides
    @Singleton
    fun provideStudyPlanDao(database: AppDatabase): StudyPlanDao {
        return database.studyPlanDao()
    }
}