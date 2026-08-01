package com.sana.app.core.network.models

import com.google.gson.annotations.SerializedName

/**
 * 🌿 SANA - DTO de Escuela
 */
data class SchoolDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("admin_code")
    val adminCode: String,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("level")
    val level: String = "MIXED",

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("created_at")
    val createdAt: Long? = null
)

fun SchoolDto.toEntity() = com.sana.app.core.database.entities.SchoolEntity(
    id = this.id ?: 0,
    name = this.name,
    code = this.code,
    adminCode = this.adminCode,
    address = this.address,
    phone = this.phone,
    email = this.email,
    country = this.country,
    level = this.level,
    isActive = this.isActive,
    createdAt = this.createdAt ?: System.currentTimeMillis()
)