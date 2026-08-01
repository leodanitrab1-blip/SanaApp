package com.sana.app.core.network.models

import com.google.gson.annotations.SerializedName

/**
 * 🌿 SANA - DTO de Mensaje para sincronización
 */
data class MessageDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("sender_id")
    val senderId: Long,

    @SerializedName("receiver_id")
    val receiverId: Long? = null,

    @SerializedName("receiver_code")
    val receiverCode: String? = null,

    @SerializedName("type")
    val type: String = "MESSAGE",

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("attachment_url")
    val attachmentUrl: String? = null,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Respuesta de sincronización
 */
data class SyncResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("synced_count")
    val syncedCount: Int = 0
)