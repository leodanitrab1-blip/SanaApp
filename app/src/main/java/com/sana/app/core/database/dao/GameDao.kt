package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.GameEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Juegos
 * 
 * Gestiona el catálogo de juegos disponibles en la plataforma.
 * Los juegos son archivos HTML/JS que se ejecutan en un WebView.
 */
@Dao
interface GameDao {

    // ============ CONSULTAS DE LECTURA ============

    /**
     * Obtener juegos por categoría, ordenados por calificación
     */
    @Query("""
        SELECT * FROM games 
        WHERE category = :category AND is_active = 1 
        ORDER BY rating DESC, play_count DESC
    """)
    fun getGamesByCategory(category: String): Flow<List<GameEntity>>

    /**
     * Obtener todos los juegos activos
     */
    @Query("SELECT * FROM games WHERE is_active = 1 ORDER BY uploaded_at DESC")
    fun getAllActiveGames(): Flow<List<GameEntity>>

    /**
     * Obtener todos los juegos (incluyendo inactivos) - para admin
     */
    @Query("SELECT * FROM games ORDER BY uploaded_at DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    /**
     * Obtener juego por ID
     */
    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getGameById(id: Long): GameEntity?

    /**
     * Buscar juegos por título o descripción
     */
    @Query("""
        SELECT * FROM games 
        WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        AND is_active = 1
        ORDER BY rating DESC
    """)
    fun searchGames(query: String): Flow<List<GameEntity>>

    /**
     * Obtener los juegos más populares (por número de veces jugado)
     */
    @Query("""
        SELECT * FROM games 
        WHERE is_active = 1 
        ORDER BY play_count DESC 
        LIMIT :limit
    """)
    fun getPopularGames(limit: Int = 10): Flow<List<GameEntity>>

    /**
     * Obtener juegos por tipo de archivo
     */
    @Query("SELECT * FROM games WHERE file_type = :fileType AND is_active = 1")
    fun getGamesByFileType(fileType: String): Flow<List<GameEntity>>

    /**
     * Contar juegos por categoría
     */
    @Query("SELECT COUNT(*) FROM games WHERE category = :category AND is_active = 1")
    suspend fun countGamesByCategory(category: String): Int

    // ============ OPERACIONES DE ESCRITURA ============

    /**
     * Insertar nuevo juego
     * @return ID del juego insertado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    /**
     * Actualizar juego existente
     */
    @Update
    suspend fun updateGame(game: GameEntity)

    /**
     * Incrementar contador de veces jugado
     */
    @Query("UPDATE games SET play_count = play_count + 1 WHERE id = :gameId")
    suspend fun incrementPlayCount(gameId: Long)

    /**
     * Actualizar calificación del juego
     */
    @Query("UPDATE games SET rating = :rating WHERE id = :gameId")
    suspend fun updateRating(gameId: Long, rating: Float)

    /**
     * Activar/desactivar juego
     */
    @Query("UPDATE games SET is_active = :isActive WHERE id = :gameId")
    suspend fun setGameActive(gameId: Long, isActive: Boolean)

    // ============ OPERACIONES DE ELIMINACIÓN ============

    /**
     * Eliminar juego por ID
     */
    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: Long)

    /**
     * Eliminar juegos por categoría
     */
    @Query("DELETE FROM games WHERE category = :category")
    suspend fun deleteGamesByCategory(category: String)
}