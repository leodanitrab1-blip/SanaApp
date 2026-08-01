package com.sana.app.core.repository

import com.sana.app.core.database.dao.MessageDao
import com.sana.app.core.database.dao.UserDao
import com.sana.app.core.database.entities.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌿 SANA - Repositorio de Mensajes
 * 
 * Sistema de mensajería interna entre usuarios de la plataforma.
 * Permite comunicación entre docentes, directores, alumnos y padres.
 */
@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val userDao: UserDao
) {

    // ============================================
    // BANDEJA DE ENTRADA
    // ============================================

    /** Mensajes recibidos */
    fun getInboxMessages(userId: Long): Flow<List<MessageEntity>> =
        messageDao.getInboxMessages(userId)

    /** Mensajes no leídos */
    fun getUnreadMessages(userId: Long): Flow<List<MessageEntity>> =
        messageDao.getUnreadMessages(userId)

    /** Contador de no leídos */
    fun getUnreadCount(userId: Long): Flow<Int> =
        messageDao.getUnreadCount(userId)

    // ============================================
    // BANDEJA DE SALIDA
    // ============================================

    /** Mensajes enviados */
    fun getSentMessages(userId: Long): Flow<List<MessageEntity>> =
        messageDao.getSentMessages(userId)

    // ============================================
    // CONVERSACIONES
    // ============================================

    /** Obtener conversación entre dos usuarios */
    fun getConversation(user1: Long, user2: Long): Flow<List<MessageEntity>> =
        messageDao.getConversation(user1, user2)

    /** Buscar mensajes */
    fun searchMessages(userId: Long, query: String): Flow<List<MessageEntity>> =
        messageDao.searchMessages(userId, query)

    // ============================================
    // OPERACIONES
    // ============================================

    /**
     * Enviar un mensaje a otro usuario
     * 
     * @param senderId ID del remitente
     * @param receiverId ID del destinatario (opcional si se usa código)
     * @param receiverCode Código del destinatario (DOC-XXXXXX)
     * @param title Título del mensaje
     * @param content Contenido del mensaje
     * @param type Tipo: MESSAGE, GUIDE, PLAN, ANNOUNCEMENT
     * @param attachmentUrl URL de archivo adjunto
     */
    suspend fun sendMessage(
        senderId: Long,
        title: String,
        content: String,
        receiverId: Long? = null,
        receiverCode: String? = null,
        type: String = "MESSAGE",
        attachmentUrl: String? = null
    ): Result<MessageEntity> {
        return try {
            // Validar que al menos un destinatario esté especificado
            if (receiverId == null && receiverCode == null) {
                return Result.failure(IllegalArgumentException("Debe especificar destinatario"))
            }

            // Si se envió por código, buscar el ID del destinatario
            var resolvedReceiverId = receiverId
            if (resolvedReceiverId == null && receiverCode != null) {
                val receiver = userDao.getUserByCode(receiverCode)
                resolvedReceiverId = receiver?.id
            }

            val message = MessageEntity(
                senderId = senderId,
                receiverId = resolvedReceiverId,
                receiverCode = receiverCode,
                type = type,
                title = title.trim(),
                content = content.trim(),
                attachmentUrl = attachmentUrl
            )

            val id = messageDao.insertMessage(message)
            Result.success(message.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Enviar mensaje a múltiples destinatarios (anuncio)
     */
    suspend fun sendAnnouncement(
        senderId: Long,
        title: String,
        content: String,
        receiverIds: List<Long>
    ): Result<Int> {
        return try {
            var sentCount = 0
            receiverIds.forEach { receiverId ->
                val message = MessageEntity(
                    senderId = senderId,
                    receiverId = receiverId,
                    type = "ANNOUNCEMENT",
                    title = title.trim(),
                    content = content.trim()
                )
                messageDao.insertMessage(message)
                sentCount++
            }
            Result.success(sentCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marcar mensaje como leído
     */
    suspend fun markAsRead(messageId: Long): Result<Unit> {
        return try {
            messageDao.markAsRead(messageId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marcar todos los mensajes de un remitente como leídos
     */
    suspend fun markAllFromSenderAsRead(userId: Long, senderId: Long): Result<Unit> {
        return try {
            messageDao.markAllFromSenderAsRead(userId, senderId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar mensaje
     */
    suspend fun deleteMessage(messageId: Long): Result<Unit> {
        return try {
            messageDao.deleteMessage(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}