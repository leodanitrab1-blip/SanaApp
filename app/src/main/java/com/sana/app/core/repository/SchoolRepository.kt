package com.sana.app.core.repository

import com.sana.app.core.database.dao.SchoolDao
import com.sana.app.core.database.dao.UserDao
import com.sana.app.core.database.entities.SchoolEntity
import com.sana.app.core.database.entities.UserEntity
import com.sana.app.core.network.ApiService
import com.sana.app.core.utils.CodeGenerator
import com.sana.app.core.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌿 SANA - Repositorio de Escuelas
 * 
 * Gestiona el registro, consulta y administración de escuelas.
 * También maneja la generación de códigos para docentes y directores.
 */
@Singleton
class SchoolRepository @Inject constructor(
    private val schoolDao: SchoolDao,
    private val userDao: UserDao,
    private val apiService: ApiService
) {

    // ============================================
    // CONSULTAS
    // ============================================

    /** Obtener todas las escuelas activas */
    fun getAllActiveSchools(): Flow<List<SchoolEntity>> = schoolDao.getAllActiveSchools()

    /** Obtener todas las escuelas (incluyendo inactivas) - Admin */
    fun getAllSchools(): Flow<List<SchoolEntity>> = schoolDao.getAllSchools()

    /** Buscar escuela por código */
    suspend fun getSchoolByCode(code: String): SchoolEntity? = schoolDao.getSchoolByCode(code)

    /** Buscar escuela por código de admin */
    suspend fun getSchoolByAdminCode(adminCode: String): SchoolEntity? =
        schoolDao.getSchoolByAdminCode(adminCode)

    /** Buscar escuelas por nombre */
    fun searchSchools(query: String): Flow<List<SchoolEntity>> = schoolDao.searchSchools(query)

    /** Obtener escuelas por país */
    fun getSchoolsByCountry(country: String): Flow<List<SchoolEntity>> =
        schoolDao.getSchoolsByCountry(country)

    /** Contar escuelas activas */
    suspend fun getActiveSchoolCount(): Int = schoolDao.getActiveSchoolCount()

    // ============================================
    // REGISTRO DE ESCUELA
    // ============================================

    /**
     * Registrar una nueva escuela y generar sus códigos
     * 
     * Crea la escuela, genera códigos DOC y ADM,
     * y opcionalmente crea el usuario director.
     * 
     * @param name Nombre de la escuela
     * @param address Dirección (opcional)
     * @param phone Teléfono (opcional)
     * @param email Email (opcional)
     * @param country País
     * @param level Nivel educativo
     * @param directorName Nombre del director para crear su usuario
     * @param directorPassword Contraseña del director
     * @return Resultado con los códigos generados
     */
    suspend fun registerSchool(
        name: String,
        address: String? = null,
        phone: String? = null,
        email: String? = null,
        country: String? = null,
        level: String = "MIXED",
        directorName: String? = null,
        directorPassword: String? = null
    ): Result<SchoolRegistrationResult> {
        return try {
            // Generar códigos únicos
            val schoolCode = generateUniqueSchoolCode()
            val adminCode = generateUniqueAdminCode()

            // Crear entidad escuela
            val school = SchoolEntity(
                name = name,
                code = schoolCode,
                adminCode = adminCode,
                address = address,
                phone = phone,
                email = email,
                country = country,
                level = level
            )

            val schoolId = schoolDao.insertSchool(school)

            // Crear usuario director si se proporcionaron datos
            var directorUser: UserEntity? = null
            if (directorName != null && directorPassword != null) {
                val teacherCode = generateUniqueTeacherCode()
                
                directorUser = UserEntity(
                    username = "dir_${schoolCode.lowercase()}",
                    passwordHash = directorPassword,
                    fullName = directorName,
                    role = Constants.ROLE_DIRECTOR,
                    accessCode = adminCode,
                    schoolCode = schoolCode,
                    email = email
                )
                
                userDao.insertUser(directorUser)
            }

            Result.success(
                SchoolRegistrationResult(
                    school = school.copy(id = schoolId),
                    schoolCode = schoolCode,
                    adminCode = adminCode,
                    directorUser = directorUser
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registrar un docente en una escuela existente
     * 
     * @param schoolCode Código de la escuela
     * @param teacherName Nombre del docente
     * @param teacherPassword Contraseña del docente
     * @return Código de acceso del docente
     */
    suspend fun registerTeacher(
        schoolCode: String,
        teacherName: String,
        teacherPassword: String
    ): Result<String> {
        return try {
            // Verificar que la escuela existe
            val school = schoolDao.getSchoolByCode(schoolCode)
                ?: return Result.failure(Exception("Escuela no encontrada"))

            val teacherCode = generateUniqueTeacherCode()

            val teacher = UserEntity(
                username = "doc_${teacherCode.lowercase()}",
                passwordHash = teacherPassword,
                fullName = teacherName,
                role = Constants.ROLE_TEACHER,
                accessCode = teacherCode,
                schoolCode = schoolCode
            )

            userDao.insertUser(teacher)
            Result.success(teacherCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // ADMINISTRACIÓN
    // ============================================

    /**
     * Dar de baja una escuela y todos sus usuarios
     */
    suspend fun deactivateSchool(schoolCode: String): Result<Unit> {
        return try {
            schoolDao.deactivateSchool(schoolCode)
            // También desactivar usuarios de la escuela
            // userDao.deactivateUsersBySchool(schoolCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar escuela completamente (solo admin maestro)
     */
    suspend fun deleteSchool(schoolCode: String): Result<Unit> {
        return try {
            // Eliminar usuarios de la escuela
            userDao.deleteUsersBySchool(schoolCode)
            // Eliminar escuela
            schoolDao.deleteSchoolByCode(schoolCode)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generar nuevo código de administrador para una escuela
     */
    suspend fun regenerateAdminCode(schoolCode: String): Result<String> {
        return try {
            val newCode = generateUniqueAdminCode()
            schoolDao.updateAdminCode(schoolCode, newCode)
            Result.success(newCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // UTILIDADES PRIVADAS
    // ============================================

    /**
     * Genera código de escuela único verificando que no exista
     */
    private suspend fun generateUniqueSchoolCode(): String {
        var code: String
        do {
            code = CodeGenerator.generateSchoolCode()
        } while (schoolDao.schoolCodeExists(code))
        return code
    }

    /**
     * Genera código de admin único
     */
    private suspend fun generateUniqueAdminCode(): String {
        var code: String
        do {
            code = CodeGenerator.generateDirectorCode()
        } while (schoolDao.getSchoolByAdminCode(code) != null)
        return code
    }

    /**
     * Genera código de docente único
     */
    private suspend fun generateUniqueTeacherCode(): String {
        var code: String
        do {
            code = CodeGenerator.generateTeacherCode()
        } while (userDao.getUserByCode(code) != null)
        return code
    }
}

/**
 * Resultado del registro de escuela
 */
data class SchoolRegistrationResult(
    val school: SchoolEntity,
    val schoolCode: String,
    val adminCode: String,
    val directorUser: UserEntity?
)