package com.sana.app.core.network.models

import com.google.gson.annotations.SerializedName

/**
 * 🌿 SANA - DTO de Metadatos de Juego
 */
data class GameMetadataDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("file_url")
    val fileUrl: String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,

    @SerializedName("file_type")
    val fileType: String = "HTML",

    @SerializedName("rating")
    val rating: Float = 0f,

    @SerializedName("play_count")
    val playCount: Int = 0
)

/**
 * Respuesta del endpoint de juegos
 */
data class GameMetadataResponse(
    @SerializedName("games")
    val games: List<GameMetadataDto>
)

fun GameMetadataDto.toEntity() = com.sana.app.core.database.entities.GameEntity(
    id = this.id ?: 0,
    title = this.title,
    description = this.description,
    category = this.category,
    fileUrl = this.fileUrl,
    thumbnailUrl = this.thumbnailUrl,
    fileType = this.fileType,
    rating = this.rating,
    playCount = this.playCount
)