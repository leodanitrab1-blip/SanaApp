package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Usuario
 * 
 * Representa a cualquier usuario del sistema: estudiantes, docentes, 
 * directores, administradores y padres de familia.
 * 
 * Índices:
 * - access_code: Búsqueda rápida por código de acceso (login sin contraseña)
 * - school_code + role: Búsqueda de usuarios por escuela y rol
 * - username: Login tradicional
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["access_code"], unique = true),
        Index(value = ["school_code", "role"]),
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    /** ID autogenerado por Room */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Nombre de usuario único para login tradicional */
    @ColumnInfo(name = "username")
    val username: String,

    /** Hash SHA-256 de la contraseña (nunca almacenamos texto plano) */
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    /** Nombre completo del usuario para mostrar en la UI */
    @ColumnInfo(name = "full_name")
    val fullName: String,

    /** 
     * Rol del usuario en el sistema:
     * STUDENT, TEACHER, DIRECTOR, ADMIN, PARENT
     */
    @ColumnInfo(name = "role")
    val role: String,

    /** 
     * Código de acceso único (ej: DOC-ABC123)
     * Permite login sin recordar usuario/contraseña
     * Puede ser null para usuarios que no usan código
     */
    @ColumnInfo(name = "access_code")
    val accessCode: String? = null,

    /** Código de la escuela a la que pertenece (si aplica) */
    @ColumnInfo(name = "school_code")
    val schoolCode: String? = null,

    /** Email del usuario para notificaciones (opcional) */
    @ColumnInfo(name = "email")
    val email: String? = null,

    /** URL del avatar del usuario (opcional) */
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    /** Si el usuario está activo (no eliminado/bloqueado) */
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    /** Timestamp de creación (epoch millis) */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** Último acceso del usuario (epoch millis) */
    @ColumnInfo(name = "last_login")
    val lastLogin: Long? = null,

    /** Token de sesión para recordar login */
    @ColumnInfo(name = "session_token")
    val sessionToken: String? = null
)