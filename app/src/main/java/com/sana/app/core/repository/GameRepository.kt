package com.sana.app.core.repository

import com.sana.app.core.database.dao.GameDao
import com.sana.app.core.database.entities.GameEntity
import com.sana.app.core.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌿 SANA - Repositorio de Juegos
 * 
 * Gestiona el catálogo de juegos HTML/JS disponibles.
 * Los juegos se almacenan como archivos en GitHub y se
 * acceden mediante WebView en la app.
 */
@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val apiService: ApiService
) {

    // ============================================
    // CONSULTAS
    // ============================================

    /** Obtener juegos por categoría */
    fun getGamesByCategory(category: String): Flow<List<GameEntity>> =
        gameDao.getGamesByCategory(category)

    /** Obtener todos los juegos activos */
    fun getAllActiveGames(): Flow<List<GameEntity>> = gameDao.getAllActiveGames()

    /** Obtener todos los juegos (admin) */
    fun getAllGames(): Flow<List<GameEntity>> = gameDao.getAllGames()

    /** Buscar juegos */
    fun searchGames(query: String): Flow<List<GameEntity>> = gameDao.searchGames(query)

    /** Obtener juegos populares */
    fun getPopularGames(limit: Int = 10): Flow<List<GameEntity>> = gameDao.getPopularGames(limit)

    /** Obtener juego por ID */
    suspend fun getGameById(id: Long): GameEntity? = gameDao.getGameById(id)

    // ============================================
    // OPERACIONES
    // ============================================

    /**
     * Subir un nuevo juego (solo admin)
     */
    suspend fun uploadGame(
        title: String,
        description: String,
        category: String,
        fileUrl: String,
        thumbnailUrl: String? = null,
        fileType: String = "HTML",
        uploadedBy: Long
    ): Result<GameEntity> {
        return try {
            // Validar categoría
            val validCategories = listOf("GENERAL", "PRIMARY", "SECONDARY")
            if (category !in validCategories) {
                return Result.failure(IllegalArgumentException("Categoría no válida"))
            }

            val game = GameEntity(
                title = title.trim(),
                description = description.trim(),
                category = category,
                fileUrl = fileUrl,
                thumbnailUrl = thumbnailUrl,
                fileType = fileType,
                uploadedBy = uploadedBy
            )

            val id = gameDao.insertGame(game)
            Result.success(game.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registrar que un juego fue jugado (incrementa contador)
     */
    suspend fun playGame(gameId: Long): Result<Unit> {
        return try {
            gameDao.incrementPlayCount(gameId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calificar un juego
     */
    suspend fun rateGame(gameId: Long, rating: Float): Result<Unit> {
        return try {
            if (rating !in 1f..5f) {
                return Result.failure(IllegalArgumentException("Calificación debe ser 1-5"))
            }
            gameDao.updateRating(gameId, rating)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Activar/desactivar juego (admin)
     */
    suspend fun toggleGameActive(gameId: Long, isActive: Boolean): Result<Unit> {
        return try {
            gameDao.setGameActive(gameId, isActive)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar juego (admin)
     */
    suspend fun deleteGame(gameId: Long): Result<Unit> {
        return try {
            gameDao.deleteGame(gameId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // SINCRONIZACIÓN
    // ============================================

    /**
     * Sincronizar catálogo de juegos con GitHub
     */
    suspend fun syncGamesWithRemote(): Result<Int> {
        return try {
            val response = apiService.getGamesMetadata()
            if (response.isSuccessful) {
                val remoteGames = response.body()?.games ?: emptyList()
                var syncedCount = 0

                remoteGames.forEach { dto ->
                    val existingGame = dto.id?.let { gameDao.getGameById(it) }
                    if (existingGame == null) {
                        gameDao.insertGame(dto.toEntity())
                        syncedCount++
                    }
                }

                Result.success(syncedCount)
            } else {
                Result.failure(Exception("Error de sincronización: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}