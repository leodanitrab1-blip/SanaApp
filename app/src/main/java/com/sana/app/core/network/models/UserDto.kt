package com.sana.app.core.network.models

import com.google.gson.annotations.SerializedName

/**
 * 🌿 SANA - DTO de Usuario (Data Transfer Object)
 * 
 * Representación de usuario para comunicación con la API.
 * Los DTOs están separados de las entidades Room para:
 * - Desacoplar la capa de red de la base de datos
 * - Manejar diferentes formatos de JSON
 * - Facilitar la evolución de la API sin afectar la BD local
 */
data class UserDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("password_hash")
    val passwordHash: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("access_code")
    val accessCode: String? = null,

    @SerializedName("school_code")
    val schoolCode: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("created_at")
    val createdAt: Long? = null
)

/**
 * Extensión para convertir DTO a Entity
 */
fun UserDto.toEntity() = com.sana.app.core.database.entities.UserEntity(
    id = this.id ?: 0,
    username = this.username,
    passwordHash = this.passwordHash,
    fullName = this.fullName,
    role = this.role,
    accessCode = this.accessCode,
    schoolCode = this.schoolCode,
    email = this.email,
    avatarUrl = this.avatarUrl,
    isActive = this.isActive,
    createdAt = this.createdAt ?: System.currentTimeMillis()
)