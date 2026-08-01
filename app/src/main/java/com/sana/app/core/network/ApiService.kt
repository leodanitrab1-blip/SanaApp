package com.sana.app.core.network

import com.sana.app.core.network.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 🌿 SANA - Servicio API Principal
 * 
 * Interfaz Retrofit para comunicación con GitHub como "base de datos".
 * GitHub almacena archivos JSON que funcionan como tablas remotas.
 * 
 * Endpoints:
 * - users.json: Datos de usuarios (respaldo)
 * - schools.json: Registro de escuelas
 * - emergency_contacts.json: Líneas de ayuda
 * - games_metadata.json: Catálogo de juegos
 * 
 * Todas las operaciones son suspendidas para uso con corrutinas.
 */
interface ApiService {

    // ============================================
    // USUARIOS
    // ============================================

    /**
     * Obtener todos los usuarios desde GitHub
     * @return Lista de usuarios en formato DTO
     */
    @Headers("Cache-Control: max-age=300")
    @GET("users.json")
    suspend fun getUsers(): Response<List<UserDto>>

    /**
     * Obtener usuario por ID
     * Nota: GitHub solo sirve archivos completos, 
     * el filtrado se hace en el repositorio
     */
    @Headers("Cache-Control: max-age=60")
    @GET("users.json")
    suspend fun getUserById(@Query("id") id: Long): Response<List<UserDto>>

    // ============================================
    // ESCUELAS
    // ============================================

    /**
     * Obtener todas las escuelas registradas
     */
    @Headers("Cache-Control: max-age=600")
    @GET("schools.json")
    suspend fun getSchools(): Response<List<SchoolDto>>

    /**
     * Obtener escuela por código
     */
    @Headers("Cache-Control: max-age=60")
    @GET("schools.json")
    suspend fun getSchoolByCode(@Query("code") code: String): Response<List<SchoolDto>>

    // ============================================
    // CONTACTOS DE EMERGENCIA
    // ============================================

    /**
     * Obtener contactos de emergencia organizados por país
     * @return Mapa de país -> lista de contactos
     */
    @Headers("Cache-Control: max-age=3600")
    @GET("emergency_contacts.json")
    suspend fun getEmergencyContacts(): Response<Map<String, List<EmergencyContactDto>>>

    // ============================================
    // JUEGOS
    // ============================================

    /**
     * Obtener metadatos de juegos disponibles
     */
    @Headers("Cache-Control: max-age=600")
    @GET("games_metadata.json")
    suspend fun getGamesMetadata(): Response<GameMetadataResponse>

    // ============================================
    // MENSAJES (Futuro: API propia)
    // ============================================

    /**
     * Sincronizar mensajes (placeholder para futura API)
     */
    @POST("messages/sync")
    suspend fun syncMessages(@Body messages: List<MessageDto>): Response<SyncResponse>
}