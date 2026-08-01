package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.SchoolEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Escuelas
 * 
 * Operaciones CRUD para la gestión de instituciones educativas.
 * Permite registrar, consultar, actualizar y eliminar escuelas.
 */
@Dao
interface SchoolDao {

    // ============ CONSULTAS DE LECTURA ============

    /**
     * Obtener todas las escuelas activas, ordenadas por nombre
     */
    @Query("SELECT * FROM schools WHERE is_active = 1 ORDER BY name ASC")
    fun getAllActiveSchools(): Flow<List<SchoolEntity>>

    /**
     * Obtener todas las escuelas (incluyendo inactivas)
     * Solo para administradores
     */
    @Query("SELECT * FROM schools ORDER BY created_at DESC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    /**
     * Buscar escuela por su código único (ESC-XXXXXX)
     */
    @Query("SELECT * FROM schools WHERE code = :code LIMIT 1")
    suspend fun getSchoolByCode(code: String): SchoolEntity?

    /**
     * Buscar escuela por su código de administrador (ADM-XXXXXX)
     */
    @Query("SELECT * FROM schools WHERE admin_code = :adminCode LIMIT 1")
    suspend fun getSchoolByAdminCode(adminCode: String): SchoolEntity?

    /**
     * Buscar escuela por ID
     */
    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getSchoolById(id: Long): SchoolEntity?

    /**
     * Buscar escuelas por nombre (búsqueda parcial)
     */
    @Query("SELECT * FROM schools WHERE name LIKE '%' || :query || '%' AND is_active = 1")
    fun searchSchools(query: String): Flow<List<SchoolEntity>>

    /**
     * Obtener escuelas por país
     */
    @Query("SELECT * FROM schools WHERE country = :country AND is_active = 1")
    fun getSchoolsByCountry(country: String): Flow<List<SchoolEntity>>

    /**
     * Obtener escuelas por nivel educativo
     */
    @Query("SELECT * FROM schools WHERE level = :level AND is_active = 1")
    fun getSchoolsByLevel(level: String): Flow<List<SchoolEntity>>

    /**
     * Contar total de escuelas activas
     */
    @Query("SELECT COUNT(*) FROM schools WHERE is_active = 1")
    suspend fun getActiveSchoolCount(): Int

    /**
     * Verificar si existe una escuela con ese código
     */
    @Query("SELECT EXISTS(SELECT 1 FROM schools WHERE code = :code)")
    suspend fun schoolCodeExists(code: String): Boolean

    // ============ OPERACIONES DE ESCRITURA ============

    /**
     * Insertar nueva escuela
     * @return ID de la escuela insertada
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSchool(school: SchoolEntity): Long

    /**
     * Actualizar datos de una escuela existente
     */
    @Update
    suspend fun updateSchool(school: SchoolEntity)

    /**
     * Actualizar código de administrador de una escuela
     */
    @Query("UPDATE schools SET admin_code = :newAdminCode WHERE code = :schoolCode")
    suspend fun updateAdminCode(schoolCode: String, newAdminCode: String)

    /**
     * Cambiar estado activo de una escuela
     */
    @Query("UPDATE schools SET is_active = :isActive WHERE code = :schoolCode")
    suspend fun setSchoolActive(schoolCode: String, isActive: Boolean)

    // ============ OPERACIONES DE ELIMINACIÓN ============

    /**
     * Eliminar escuela por código (borrado físico)
     */
    @Query("DELETE FROM schools WHERE code = :code")
    suspend fun deleteSchoolByCode(code: String)

    /**
     * Dar de baja lógica (no elimina, solo desactiva)
     */
    @Query("UPDATE schools SET is_active = 0 WHERE code = :schoolCode")
    suspend fun deactivateSchool(schoolCode: String)

    /**
     * Reactivar escuela dada de baja
     */
    @Query("UPDATE schools SET is_active = 1 WHERE code = :schoolCode")
    suspend fun reactivateSchool(schoolCode: String)
}