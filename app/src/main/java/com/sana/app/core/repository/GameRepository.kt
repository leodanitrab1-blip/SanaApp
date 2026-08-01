package com.sana.app.core.repository

import com.sana.app.core.database.dao.GameDao
import com.sana.app.core.database.entities.GameEntity
import com.sana.app.core.network.ApiService
import com.sana.app.core.network.models.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val apiService: ApiService
) {
    fun getGamesByCategory(category: String): Flow<List<GameEntity>> = gameDao.getGamesByCategory(category)
    fun getAllActiveGames(): Flow<List<GameEntity>> = gameDao.getAllActiveGames()
    fun getAllGames(): Flow<List<GameEntity>> = gameDao.getAllGames()
    fun searchGames(query: String): Flow<List<GameEntity>> = gameDao.searchGames(query)
    fun getPopularGames(limit: Int = 10): Flow<List<GameEntity>> = gameDao.getPopularGames(limit)
    suspend fun getGameById(id: Long): GameEntity? = gameDao.getGameById(id)

    suspend fun uploadGame(title: String, description: String, category: String, fileUrl: String, thumbnailUrl: String? = null, fileType: String = "HTML", uploadedBy: Long): Result<GameEntity> {
        return try {
            val game = GameEntity(title = title.trim(), description = description.trim(), category = category, fileUrl = fileUrl, thumbnailUrl = thumbnailUrl, fileType = fileType, uploadedBy = uploadedBy)
            val id = gameDao.insertGame(game)
            Result.success(game.copy(id = id))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun playGame(gameId: Long): Result<Unit> {
        return try { gameDao.incrementPlayCount(gameId); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun rateGame(gameId: Long, rating: Float): Result<Unit> {
        return try { gameDao.updateRating(gameId, rating); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleGameActive(gameId: Long, isActive: Boolean): Result<Unit> {
        return try { gameDao.setGameActive(gameId, isActive); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGame(gameId: Long): Result<Unit> {
        return try { gameDao.deleteGame(gameId); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
