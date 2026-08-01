package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Contactos de Emergencia
 * 
 * Gestiona las líneas de ayuda y contactos de emergencia
 * organizados por país y categoría.
 */
@Dao
interface EmergencyContactDao {

    /**
     * Obtener todos los contactos activos
     */
    @Query("SELECT * FROM emergency_contacts WHERE is_active = 1 ORDER BY country, category")
    fun getAllActiveContacts(): Flow<List<EmergencyContactEntity>>

    /**
     * Obtener contactos por país
     */
    @Query("SELECT * FROM emergency_contacts WHERE country = :country AND is_active = 1")
    fun getContactsByCountry(country: String): Flow<List<EmergencyContactEntity>>

    /**
     * Obtener contactos por categoría
     */
    @Query("SELECT * FROM emergency_contacts WHERE category = :category AND is_active = 1")
    fun getContactsByCategory(category: String): Flow<List<EmergencyContactEntity>>

    /**
     * Buscar contactos por nombre o descripción
     */
    @Query("""
        SELECT * FROM emergency_contacts 
        WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        AND is_active = 1
    """)
    fun searchContacts(query: String): Flow<List<EmergencyContactEntity>>

    /**
     * Obtener países disponibles
     */
    @Query("SELECT DISTINCT country FROM emergency_contacts WHERE is_active = 1 ORDER BY country")
    fun getAvailableCountries(): Flow<List<String>>

    /**
     * Insertar o actualizar contacto
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity): Long

    /**
     * Insertar múltiples contactos
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContactEntity>)

    /**
     * Eliminar contacto
     */
    @Query("DELETE FROM emergency_contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: Long)
}