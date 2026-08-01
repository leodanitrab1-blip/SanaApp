package com.sana.app.core.utils

import kotlin.random.Random

/**
 * 🌿 SANA - Generador de Códigos Únicos
 * 
 * Genera códigos aleatorios para identificación de escuelas,
 * docentes, directores, alumnos y padres.
 * 
 * Formato: PREFIJO-XXXXXX donde:
 * - PREFIJO: 3 letras identificando el rol
 * - XXXXXX: 6 caracteres alfanuméricos (A-Z, 0-9)
 * 
 * Ejemplos:
 * - ESC-A1B2C3 (escuela)
 * - DOC-X9Y8Z7 (docente)
 * - ADM-M4N5O6 (director/admin escuela)
 * - PAD-P7Q8R9 (padre de familia)
 * - STU-S1T2U3 (estudiante)
 */
object CodeGenerator {

    /** Caracteres permitidos en los códigos (sin vocales para evitar palabras) */
    private const val CHARS = "BCDFGHJKLMNPQRSTVWXYZ0123456789"
    
    /** Longitud del sufijo aleatorio */
    private const val SUFFIX_LENGTH = 6

    /**
     * Genera código de escuela: ESC-XXXXXX
     */
    fun generateSchoolCode(): String {
        return "${Constants.SCHOOL_PREFIX}-${randomSuffix()}"
    }

    /**
     * Genera código de docente: DOC-XXXXXX
     */
    fun generateTeacherCode(): String {
        return "${Constants.TEACHER_PREFIX}-${randomSuffix()}"
    }

    /**
     * Genera código de director/administrador de escuela: ADM-XXXXXX
     */
    fun generateDirectorCode(): String {
        return "${Constants.DIRECTOR_PREFIX}-${randomSuffix()}"
    }

    /**
     * Genera código de padre de familia: PAD-XXXXXX
     */
    fun generateParentCode(): String {
        return "${Constants.PARENT_PREFIX}-${randomSuffix()}"
    }

    /**
     * Genera código de estudiante: STU-XXXXXX
     */
    fun generateStudentCode(): String {
        return "${Constants.STUDENT_PREFIX}-${randomSuffix()}"
    }

    /**
     * Genera un código con el prefijo especificado
     * 
     * @param prefix Prefijo del código (3 letras)
     * @return Código completo PREFIJO-XXXXXX
     * @throws IllegalArgumentException si el prefijo no es válido
     */
    fun generateCode(prefix: String): String {
        val validPrefixes = listOf(
            Constants.SCHOOL_PREFIX,
            Constants.TEACHER_PREFIX,
            Constants.DIRECTOR_PREFIX,
            Constants.PARENT_PREFIX,
            Constants.STUDENT_PREFIX
        )
        
        require(prefix in validPrefixes) {
            "Prefijo inválido: $prefix. Debe ser uno de: $validPrefixes"
        }
        
        return "$prefix-${randomSuffix()}"
    }

    /**
     * Genera múltiples códigos únicos del mismo tipo
     * 
     * @param prefix Prefijo del código
     * @param count Cantidad de códigos a generar
     * @return Lista de códigos únicos
     */
    fun generateBatch(prefix: String, count: Int): List<String> {
        val codes = mutableSetOf<String>()
        while (codes.size < count) {
            codes.add(generateCode(prefix))
        }
        return codes.toList()
    }

    /**
     * Genera un sufijo aleatorio de 6 caracteres
     */
    private fun randomSuffix(): String {
        return (1..SUFFIX_LENGTH)
            .map { CHARS[Random.nextInt(CHARS.length)] }
            .joinToString("")
    }

    /**
     * Valida el formato de un código
     * 
     * @param code Código a validar
     * @param prefix Prefijo esperado
     * @return true si el formato es válido
     */
    fun validateCodeFormat(code: String, prefix: String): Boolean {
        val pattern = Regex("^$prefix-[A-Z0-9]{$SUFFIX_LENGTH}$")
        return pattern.matches(code)
    }

    /**
     * Valida cualquier código de Sana sin importar el prefijo
     */
    fun isValidSanaCode(code: String): Boolean {
        val pattern = Regex("^(ESC|DOC|ADM|PAD|STU)-[A-Z0-9]{$SUFFIX_LENGTH}$")
        return pattern.matches(code)
    }

    /**
     * Extrae el prefijo de un código
     * 
     * @return Prefijo (ESC, DOC, ADM, PAD, STU) o null si no es válido
     */
    fun extractPrefix(code: String): String? {
        return if (isValidSanaCode(code)) {
            code.substring(0, 3)
        } else {
            null
        }
    }

    /**
     * Determina el rol basado en el prefijo del código
     */
    fun codeToRole(code: String): String? {
        return when (extractPrefix(code)) {
            Constants.SCHOOL_PREFIX -> Constants.ROLE_DIRECTOR
            Constants.TEACHER_PREFIX -> Constants.ROLE_TEACHER
            Constants.DIRECTOR_PREFIX -> Constants.ROLE_DIRECTOR
            Constants.PARENT_PREFIX -> Constants.ROLE_PARENT
            Constants.STUDENT_PREFIX -> Constants.ROLE_STUDENT
            else -> null
        }
    }
}