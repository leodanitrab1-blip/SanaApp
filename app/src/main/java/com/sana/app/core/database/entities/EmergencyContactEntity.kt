package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Contacto de Emergencia
 * 
 * Líneas de ayuda y contactos de emergencia organizados por país.
 * 45+ contactos en 8 países.
 */
@Entity(
    tableName = "emergency_contacts",
    indices = [
        Index(value = ["country"]),
        Index(value = ["category"])
    ]
)
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Nombre de la línea de ayuda */
    @ColumnInfo(name = "name")
    val name: String,

    /** Número de teléfono */
    @ColumnInfo(name = "phone")
    val phone: String,

    /** Descripción del servicio */
    @ColumnInfo(name = "description")
    val description: String? = null,

    /** País donde está disponible */
    @ColumnInfo(name = "country")
    val country: String,

    /** Categoría: SUICIDE, VIOLENCE, ANXIETY, GENERAL, LGBTQ */
    @ColumnInfo(name = "category")
    val category: String = "GENERAL",

    /** URL del sitio web (si aplica) */
    @ColumnInfo(name = "website")
    val website: String? = null,

    /** Si es línea gratuita */
    @ColumnInfo(name = "is_free", defaultValue = "1")
    val isFree: Boolean = true,

    /** Horario de atención: 24_7, BUSINESS_HOURS, etc */
    @ColumnInfo(name = "schedule")
    val schedule: String = "24_7",

    /** Si está activa en el sistema */
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true
)