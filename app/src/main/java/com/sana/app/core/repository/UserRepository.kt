package com.sana.app.core.repository

import com.sana.app.core.database.dao.UserDao
import com.sana.app.core.database.entities.UserEntity
import com.sana.app.core.network.ApiService
import com.sana.app.core.network.models.toEntity
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.sha256
import kotlinx.coroutines.flow.Flow
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌿 SANA - Repositorio de Usuarios
 * 
 * Orquesta las operaciones entre la base de datos local (Room)
 * y la base de datos remota (GitHub JSON).
 * 
 * Estrategia:
 * 1. Primero intenta operación local (rápida, offline)
 * 2. Sincroniza con remoto cuando hay conexión
 * 3. Resuelve conflictos favoreciendo el dato más reciente
 * 
 * @param userDao DAO de Room para operaciones locales
 * @param apiService Servicio Retrofit para operaciones remotas
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService
) {

    // ============================================
    // AUTENTICACIÓN
    // ============================================

    /**
     * Login tradicional con usuario y contraseña
     * 
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano (se hashea internamente)
     * @return UserEntity si las credenciales son válidas, null si no
     */
    suspend fun login(username: String, password: String): Result<UserEntity> {
        return try {
            val passwordHash = password.sha256()
            val user = userDao.login(username, passwordHash)
            
            if (user != null) {
                // Actualizar último login y generar token de sesión
                val sessionToken = generateSessionToken()
                userDao.updateLoginInfo(
                    userId = user.id,
                    timestamp = System.currentTimeMillis(),
                    token = sessionToken
                )
                Result.success(user.copy(sessionToken = sessionToken))
            } else {
                Result.failure(AuthException("Usuario o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(AuthException("Error de autenticación: ${e.message}"))
        }
    }

    /**
     * Login por código de acceso (DOC-XXXXXX, ESC-XXXXXX, etc.)
     * No requiere contraseña, solo el código único
     */
    suspend fun loginByCode(code: String): Result<UserEntity> {
        return try {
            // Validar formato del código según el prefijo
            val prefix = code.take(3)
            if (prefix !in listOf("DOC", "ESC", "ADM", "PAD", "STU", "SAN")) {
                return Result.failure(AuthException("Formato de código inválido"))
            }

            val user = userDao.getUserByCode(code)
            
            if (user != null) {
                val sessionToken = generateSessionToken()
                userDao.updateLoginInfo(
                    userId = user.id,
                    timestamp = System.currentTimeMillis(),
                    token = sessionToken
                )
                Result.success(user.copy(sessionToken = sessionToken))
            } else {
                Result.failure(AuthException("Código no encontrado o inactivo"))
            }
        } catch (e: Exception) {
            Result.failure(AuthException("Error al iniciar sesión: ${e.message}"))
        }
    }

    /**
     * Login de administrador maestro
     * Código fijo: SANA-ADMIN-2025
     */
    suspend fun adminLogin(masterCode: String): Result<UserEntity> {
        return try {
            if (masterCode != Constants.ADMIN_MASTER_CODE) {
                return Result.failure(AuthException("Código de administrador inválido"))
            }

            // Buscar o crear usuario admin
            val existingAdmin = userDao.getUserByCode(masterCode)
            
            if (existingAdmin != null) {
                val sessionToken = generateSessionToken()
                userDao.updateLoginInfo(
                    userId = existingAdmin.id,
                    timestamp = System.currentTimeMillis(),
                    token = sessionToken
                )
                Result.success(existingAdmin.copy(sessionToken = sessionToken))
            } else {
                // Crear usuario administrador por primera vez
                val adminUser = UserEntity(
                    username = "admin",
                    passwordHash = "admin123".sha256(),
                    fullName = "Administrador Sana",
                    role = Constants.ROLE_ADMIN,
                    accessCode = masterCode
                )
                val id = userDao.insertUser(adminUser)
                Result.success(adminUser.copy(id = id))
            }
        } catch (e: Exception) {
            Result.failure(AuthException("Error de administrador: ${e.message}"))
        }
    }

    /**
     * Validar token de sesión para auto-login
     */
    suspend fun validateSession(userId: Long, token: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByIdSync(userId)
            if (user != null && user.sessionToken == token && user.isActive) {
                Result.success(user)
            } else {
                Result.failure(AuthException("Sesión expirada"))
            }
        } catch (e: Exception) {
            Result.failure(AuthException("Error de sesión: ${e.message}"))
        }
    }

    /**
     * Cerrar sesión (limpiar token)
     */
    suspend fun logout(userId: Long) {
        userDao.updateSessionToken(userId, null)
    }

    // ============================================
    // CONSULTAS
    // ============================================

    /**
     * Obtener usuario por ID con observación reactiva
     */
    fun getUserById(id: Long): Flow<UserEntity?> = userDao.getUserById(id)

    /**
     * Obtener usuarios por rol
     */
    fun getUsersByRole(role: String): Flow<List<UserEntity>> = userDao.getUsersByRole(role)

    /**
     * Obtener usuarios de una escuela
     */
    fun getUsersBySchool(schoolCode: String): Flow<List<UserEntity>> =
        userDao.getUsersBySchool(schoolCode)

    /**
     * Buscar usuarios por nombre
     */
    fun searchUsers(query: String): Flow<List<UserEntity>> = userDao.searchUsers(query)

    /**
     * Obtener todos los usuarios
     */
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    // ============================================
    // OPERACIONES CRUD
    // ============================================

    /**
     * Crear nuevo usuario con contraseña hasheada
     */
    suspend fun createUser(
        username: String,
        password: String,
        fullName: String,
        role: String,
        accessCode: String? = null,
        schoolCode: String? = null,
        email: String? = null
    ): Result<UserEntity> {
        return try {
            val user = UserEntity(
                username = username,
                passwordHash = password.sha256(),
                fullName = fullName,
                role = role,
                accessCode = accessCode,
                schoolCode = schoolCode,
                email = email
            )
            val id = userDao.insertUser(user)
            Result.success(user.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar datos de usuario
     */
    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Dar de baja lógica (no elimina físicamente)
     */
    suspend fun deactivateUser(userId: Long): Result<Unit> {
        return try {
            userDao.deactivateUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reactivar usuario dado de baja
     */
    suspend fun activateUser(userId: Long): Result<Unit> {
        return try {
            userDao.setUserActive(userId, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar usuario físicamente (solo admin)
     */
    suspend fun deleteUser(userId: Long): Result<Unit> {
        return try {
            userDao.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // SINCRONIZACIÓN
    // ============================================

    /**
     * Sincronizar usuarios locales con remotos
     * Descarga datos de GitHub y actualiza la BD local
     */
    suspend fun syncWithRemote(): Result<Int> {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                val remoteUsers = response.body() ?: emptyList()
                var syncedCount = 0

                remoteUsers.forEach { dto ->
                    val localUser = dto.id?.let { userDao.getUserByIdSync(it) }
                    
                    if (localUser == null || dto.createdAt ?: 0 > localUser.createdAt) {
                        userDao.insertUser(dto.toEntity())
                        syncedCount++
                    }
                }
                
                Result.success(syncedCount)
            } else {
                Result.failure(SyncException("Error de sincronización: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(SyncException("Error de red: ${e.message}"))
        }
    }

    // ============================================
    // UTILIDADES PRIVADAS
    // ============================================

    /**
     * Genera un token de sesión único y seguro
     */
    private fun generateSessionToken(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * Excepción personalizada para errores de autenticación
 */
class AuthException(message: String) : Exception(message)

/**
 * Excepción personalizada para errores de sincronización
 */
class SyncException(message: String) : Exception(message)