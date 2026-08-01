package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Mensajes
 * 
 * Sistema de mensajería interna para comunicación entre
 * docentes, directores, alumnos y padres.
 */
@Dao
interface MessageDao {

    // ============ BANDEJA DE ENTRADA ============

    /**
     * Obtener mensajes recibidos por un usuario
     */
    @Query("""
        SELECT * FROM messages 
        WHERE receiver_id = :userId 
        ORDER BY timestamp DESC
    """)
    fun getInboxMessages(userId: Long): Flow<List<MessageEntity>>

    /**
     * Obtener mensajes no leídos
     */
    @Query("""
        SELECT * FROM messages 
        WHERE receiver_id = :userId AND is_read = 0 
        ORDER BY timestamp DESC
    """)
    fun getUnreadMessages(userId: Long): Flow<List<MessageEntity>>

    /**
     * Contar mensajes no leídos
     */
    @Query("SELECT COUNT(*) FROM messages WHERE receiver_id = :userId AND is_read = 0")
    fun getUnreadCount(userId: Long): Flow<Int>

    // ============ BANDEJA DE SALIDA ============

    /**
     * Obtener mensajes enviados por un usuario
     */
    @Query("""
        SELECT * FROM messages 
        WHERE sender_id = :userId 
        ORDER BY timestamp DESC
    """)
    fun getSentMessages(userId: Long): Flow<List<MessageEntity>>

    // ============ CONSULTAS ESPECÍFICAS ============

    /**
     * Obtener mensaje por ID
     */
    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: Long): MessageEntity?

    /**
     * Buscar mensajes por título o contenido
     */
    @Query("""
        SELECT * FROM messages 
        WHERE (receiver_id = :userId OR sender_id = :userId)
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchMessages(userId: Long, query: String): Flow<List<MessageEntity>>

    /**
     * Obtener mensajes por tipo (MESSAGE, GUIDE, PLAN, ANNOUNCEMENT)
     */
    @Query("""
        SELECT * FROM messages 
        WHERE receiver_id = :userId AND type = :type
        ORDER BY timestamp DESC
    """)
    fun getMessagesByType(userId: Long, type: String): Flow<List<MessageEntity>>

    /**
     * Obtener conversación entre dos usuarios
     */
    @Query("""
        SELECT * FROM messages 
        WHERE (sender_id = :user1 AND receiver_id = :user2)
        OR (sender_id = :user2 AND receiver_id = :user1)
        ORDER BY timestamp ASC
    """)
    fun getConversation(user1: Long, user2: Long): Flow<List<MessageEntity>>

    /**
     * Obtener mensajes enviados a un código específico
     */
    @Query("""
        SELECT * FROM messages 
        WHERE receiver_code = :code 
        ORDER BY timestamp DESC
    """)
    fun getMessagesByReceiverCode(code: String): Flow<List<MessageEntity>>

    // ============ OPERACIONES DE ESCRITURA ============

    /**
     * Insertar nuevo mensaje
     * @return ID del mensaje insertado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    /**
     * Marcar mensaje como leído
     */
    @Query("""
        UPDATE messages 
        SET is_read = 1, read_at = :readAt 
        WHERE id = :messageId
    """)
    suspend fun markAsRead(messageId: Long, readAt: Long)

    /**
     * Marcar todos los mensajes de un remitente como leídos
     */
    @Query("""
        UPDATE messages 
        SET is_read = 1, read_at = :readAt 
        WHERE receiver_id = :userId AND sender_id = :senderId AND is_read = 0
    """)
    suspend fun markAllFromSenderAsRead(userId: Long, senderId: Long, readAt: Long)

    /**
     * Actualizar mensaje (editar contenido)
     */
    @Update
    suspend fun updateMessage(message: MessageEntity)

    // ============ OPERACIONES DE ELIMINACIÓN ============

    /**
     * Eliminar mensaje por ID
     */
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)

    /**
     * Eliminar conversación completa entre dos usuarios
     */
    @Query("""
        DELETE FROM messages 
        WHERE (sender_id = :user1 AND receiver_id = :user2)
        OR (sender_id = :user2 AND receiver_id = :user1)
    """)
    suspend fun deleteConversation(user1: Long, user2: Long)

    /**
     * Eliminar todos los mensajes de un usuario
     */
    @Query("DELETE FROM messages WHERE receiver_id = :userId OR sender_id = :userId")
    suspend fun deleteAllMessagesForUser(userId: Long)
}