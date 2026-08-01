package com.sana.app.core.network.models

import com.google.gson.annotations.SerializedName

/**
 * 🌿 SANA - DTO de Contacto de Emergencia
 */
data class EmergencyContactDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("country")
    val country: String = "México",

    @SerializedName("category")
    val category: String = "GENERAL",

    @SerializedName("website")
    val website: String? = null,

    @SerializedName("is_free")
    val isFree: Boolean = true,

    @SerializedName("schedule")
    val schedule: String = "24_7"
)

fun EmergencyContactDto.toEntity() = com.sana.app.core.database.entities.EmergencyContactEntity(
    name = this.name,
    phone = this.phone,
    description = this.description,
    country = this.country,
    category = this.category,
    website = this.website,
    isFree = this.isFree,
    schedule = this.schedule
)