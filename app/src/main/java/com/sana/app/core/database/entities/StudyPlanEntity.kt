package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Plan de Estudio
 * 
 * Planes de estudio creados por docentes y compartidos
 * con alumnos o hechos públicos.
 */
@Entity(
    tableName = "study_plans",
    indices = [
        Index(value = ["creator_id", "timestamp"]),
        Index(value = ["visibility"]),
        Index(value = ["subject"])
    ]
)
data class StudyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID del docente que creó el plan */
    @ColumnInfo(name = "creator_id")
    val creatorId: Long,

    /** Título del plan de estudio */
    @ColumnInfo(name = "title")
    val title: String,

    /** Descripción del plan */
    @ColumnInfo(name = "description")
    val description: String,

    /** Materia o asignatura */
    @ColumnInfo(name = "subject")
    val subject: String,

    /** Grado o nivel educativo */
    @ColumnInfo(name = "grade")
    val grade: String? = null,

    /** 
     * Visibilidad del plan:
     * PRIVATE, SCHOOL, PUBLIC
     */
    @ColumnInfo(name = "visibility")
    val visibility: String = "PRIVATE",

    /** URL del archivo adjunto (PDF) */
    @ColumnInfo(name = "attachment_url")
    val attachmentUrl: String? = null,

    /** Código para compartir con otros docentes */
    @ColumnInfo(name = "share_code")
    val shareCode: String? = null,

    /** Si está activo */
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    /** Timestamp de creación */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)