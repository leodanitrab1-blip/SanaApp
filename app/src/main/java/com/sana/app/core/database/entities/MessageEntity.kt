package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Mensaje
 * 
 * Sistema de mensajería interna entre usuarios.
 * Permite comunicación entre docentes, directores y alumnos.
 */
@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["sender_id", "timestamp"]),
        Index(value = ["receiver_id", "is_read"]),
        Index(value = ["receiver_code"])
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID del usuario que envía el mensaje */
    @ColumnInfo(name = "sender_id")
    val senderId: Long,

    /** ID del usuario que recibe (null si se envía por código) */
    @ColumnInfo(name = "receiver_id")
    val receiverId: Long? = null,

    /** Código del destinatario (ej: DOC-XXXXXX) */
    @ColumnInfo(name = "receiver_code")
    val receiverCode: String? = null,

    /** Tipo de mensaje: MESSAGE, GUIDE, PLAN, ANNOUNCEMENT */
    @ColumnInfo(name = "type")
    val type: String = "MESSAGE",

    /** Título del mensaje */
    @ColumnInfo(name = "title")
    val title: String,

    /** Contenido del mensaje */
    @ColumnInfo(name = "content")
    val content: String,

    /** URL de archivo adjunto (PDF, imagen) */
    @ColumnInfo(name = "attachment_url")
    val attachmentUrl: String? = null,

    /** Si el mensaje ha sido leído por el destinatario */
    @ColumnInfo(name = "is_read", defaultValue = "0")
    val isRead: Boolean = false,

    /** Timestamp de envío */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    /** Timestamp de lectura */
    @ColumnInfo(name = "read_at")
    val readAt: Long? = null
)