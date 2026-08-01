package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Usuarios
 * 
 * Operaciones de base de datos para la entidad UserEntity.
 * Todas las operaciones de escritura son suspendidas (corrutinas).
 * Las lecturas retornan Flow para observación reactiva.
 */
@Dao
interface UserDao {

    // ============ CONSULTAS DE LECTURA ============

    /**
     * Login tradicional con usuario y contraseña
     * @return Usuario si las credenciales son válidas, null si no
     */
    @Query("""
        SELECT * FROM users 
        WHERE username = :username 
        AND password_hash = :passwordHash 
        AND is_active = 1 
        LIMIT 1
    """)
    suspend fun login(username: String, passwordHash: String): UserEntity?

    /**
     * Login por código de acceso (DOC-XXXXXX, ESC-XXXXXX, etc)
     * No requiere contraseña
     */
    @Query("""
        SELECT * FROM users 
        WHERE access_code = :code 
        AND is_active = 1 
        LIMIT 1
    """)
    suspend fun getUserByCode(code: String): UserEntity?

    /**
     * Obtener usuario por ID con observación reactiva
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    /**
     * Obtener usuario por ID (versión suspendida)
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserEntity?

    /**
     * Obtener todos los usuarios de un rol específico
     */
    @Query("SELECT * FROM users WHERE role = :role AND is_active = 1")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    /**
     * Obtener usuarios de una escuela específica
     */
    @Query("SELECT * FROM users WHERE school_code = :schoolCode AND is_active = 1")
    fun getUsersBySchool(schoolCode: String): Flow<List<UserEntity>>

    /**
     * Buscar usuarios por nombre (búsqueda parcial)
     */
    @Query("SELECT * FROM users WHERE full_name LIKE '%' || :query || '%' AND is_active = 1")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    /**
     * Obtener todos los usuarios activos
     */
    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Contar usuarios por rol
     */
    @Query("SELECT COUNT(*) FROM users WHERE role = :role AND is_active = 1")
    suspend fun countUsersByRole(role: String): Int

    // ============ OPERACIONES DE ESCRITURA ============

    /**
     * Insertar o actualizar usuario
     * Si el ID existe, actualiza; si no, inserta
     * @return ID del usuario insertado/actualizado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Insertar múltiples usuarios (para importación)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Actualizar usuario existente
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Actualizar último login
     */
    @Query("UPDATE users SET last_login = :timestamp, session_token = :token WHERE id = :userId")
    suspend fun updateLoginInfo(userId: Long, timestamp: Long, token: String)

    /**
     * Actualizar token de sesión
     */
    @Query("UPDATE users SET session_token = :token WHERE id = :userId")
    suspend fun updateSessionToken(userId: Long, token: String?)

    /**
     * Cambiar estado activo del usuario (baja/alta)
     */
    @Query("UPDATE users SET is_active = :isActive WHERE id = :userId")
    suspend fun setUserActive(userId: Long, isActive: Boolean)

    // ============ OPERACIONES DE ELIMINACIÓN ============

    /**
     * Eliminar usuario por ID (borrado físico)
     */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    /**
     * Dar de baja lógica (no elimina, solo desactiva)
     */
    @Query("UPDATE users SET is_active = 0 WHERE id = :userId")
    suspend fun deactivateUser(userId: Long)

    /**
     * Eliminar todos los usuarios de una escuela
     */
    @Query("DELETE FROM users WHERE school_code = :schoolCode")
    suspend fun deleteUsersBySchool(schoolCode: String)
}