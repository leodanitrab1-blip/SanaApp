package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Escuela
 * 
 * Representa una institución educativa registrada en el sistema.
 * Cada escuela tiene códigos únicos para identificación y administración.
 */
@Entity(
    tableName = "schools",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["admin_code"], unique = true)
    ]
)
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Nombre completo de la institución */
    @ColumnInfo(name = "name")
    val name: String,

    /** 
     * Código único de la escuela (ESC-XXXXXX)
     * Usado por el director para acceder
     */
    @ColumnInfo(name = "code")
    val code: String,

    /** 
     * Código de administrador de la escuela (ADM-XXXXXX)
     * Usado por el administrador de la escuela
     */
    @ColumnInfo(name = "admin_code")
    val adminCode: String,

    /** Dirección física de la escuela (opcional) */
    @ColumnInfo(name = "address")
    val address: String? = null,

    /** Número de teléfono de contacto (opcional) */
    @ColumnInfo(name = "phone")
    val phone: String? = null,

    /** Email de contacto (opcional) */
    @ColumnInfo(name = "email")
    val email: String? = null,

    /** País donde se ubica la escuela */
    @ColumnInfo(name = "country")
    val country: String? = null,

    /** Nivel educativo: PRIMARY, SECONDARY, HIGH_SCHOOL, MIXED */
    @ColumnInfo(name = "level")
    val level: String = "MIXED",

    /** Si la escuela está activa en el sistema */
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    /** Timestamp de creación */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** Código del administrador SANA que registró la escuela */
    @ColumnInfo(name = "registered_by")
    val registeredBy: String? = null
)