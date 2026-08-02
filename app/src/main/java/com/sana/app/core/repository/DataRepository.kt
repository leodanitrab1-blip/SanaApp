package com.sana.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.sana.app.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Modelos de datos
data class UserRecord(
    val code: String,
    val role: String,
    val name: String,
    val schoolCode: String = "",
    val email: String = "",
    val active: Boolean = true,
    val createdAt: String = ""
)

data class SchoolRecord(
    val code: String,
    val name: String,
    val adminCode: String,
    val directorName: String,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val active: Boolean = true,
    val createdAt: String = ""
)

data class ReportRecord(
    val type: String,
    val description: String,
    val schoolCode: String,
    val timestamp: String = ""
)

@Singleton
class DataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences("sana_sync", Context.MODE_PRIVATE)
    private val baseUrl = BuildConfig.GITHUB_RAW_URL
    
    // ============ USUARIOS ============
    
    suspend fun getAllUsers(): List<UserRecord> = withContext(Dispatchers.IO) {
        try {
            val json = fetchJson("users.json")
            val type = object : TypeToken<List<UserRecord>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            loadLocalUsers()
        }
    }
    
    suspend fun saveUser(user: UserRecord): Boolean = withContext(Dispatchers.IO) {
        try {
            val users = getAllUsers().toMutableList()
            users.removeAll { it.code == user.code }
            users.add(user)
            saveLocalUsers(users)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun findUserByCode(code: String): UserRecord? = withContext(Dispatchers.IO) {
        getAllUsers().find { it.code == code && it.active }
    }
    
    suspend fun deactivateUser(code: String, reason: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val users = getAllUsers().toMutableList()
            val index = users.indexOfFirst { it.code == code }
            if (index >= 0) {
                users[index] = users[index].copy(active = false)
                saveLocalUsers(users)
                
                // Generar reporte
                val user = users[index]
                saveReport(ReportRecord(
                    type = "BAJA_USUARIO",
                    description = "Baja de ${user.role}: ${user.name} ($code) - Motivo: $reason",
                    schoolCode = user.schoolCode
                ))
                true
            } else false
        } catch (e: Exception) { false }
    }
    
    // ============ ESCUELAS ============
    
    suspend fun getAllSchools(): List<SchoolRecord> = withContext(Dispatchers.IO) {
        try {
            val json = fetchJson("schools.json")
            val type = object : TypeToken<List<SchoolRecord>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            loadLocalSchools()
        }
    }
    
    suspend fun saveSchool(school: SchoolRecord): Boolean = withContext(Dispatchers.IO) {
        try {
            val schools = getAllSchools().toMutableList()
            schools.removeAll { it.code == school.code }
            schools.add(school)
            saveLocalSchools(schools)
            true
        } catch (e: Exception) { false }
    }
    
    suspend fun deactivateSchool(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val schools = getAllSchools().toMutableList()
            val index = schools.indexOfFirst { it.code == code }
            if (index >= 0) {
                schools[index] = schools[index].copy(active = false)
                saveLocalSchools(schools)
                saveReport(ReportRecord(
                    type = "BAJA_ESCUELA",
                    description = "Escuela dada de baja: ${schools[index].name} ($code)",
                    schoolCode = code
                ))
                true
            } else false
        } catch (e: Exception) { false }
    }
    
    // ============ REPORTES ============
    
    suspend fun saveReport(report: ReportRecord) {
        try {
            val reports = loadLocalReports().toMutableList()
            reports.add(report.copy(timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())))
            val json = gson.toJson(reports)
            prefs.edit().putString("reports", json).apply()
            
            // Enviar por email (simulado - se guarda local)
            sendEmailReport(report)
        } catch (e: Exception) { }
    }
    
    private fun sendEmailReport(report: ReportRecord) {
        // En producción, aquí se enviaría el email a pdabasel1@gmail.com
        // Por ahora guardamos en un log local
        val emailLog = prefs.getString("email_log", "") ?: ""
        val entry = "[${report.timestamp}] ${report.type}: ${report.description}\n"
        prefs.edit().putString("email_log", emailLog + entry).apply()
    }
    
    fun getPendingReports(): List<ReportRecord> {
        return loadLocalReports()
    }
    
    fun getEmailLog(): String {
        return prefs.getString("email_log", "No hay reportes pendientes.") ?: ""
    }
    
    // ============ MÉTODOS PRIVADOS ============
    
    private suspend fun fetchJson(filename: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("${baseUrl}${filename}")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.readText()
            } else {
                "[]"
            }
        } catch (e: Exception) {
            // Offline: cargar de cache local
            when (filename) {
                "users.json" -> gson.toJson(loadLocalUsers())
                "schools.json" -> gson.toJson(loadLocalSchools())
                else -> "[]"
            }
        }
    }
    
    private fun loadLocalUsers(): List<UserRecord> {
        val json = prefs.getString("users_cache", "[]") ?: "[]"
        val type = object : TypeToken<List<UserRecord>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    private fun saveLocalUsers(users: List<UserRecord>) {
        prefs.edit().putString("users_cache", gson.toJson(users)).apply()
    }
    
    private fun loadLocalSchools(): List<SchoolRecord> {
        val json = prefs.getString("schools_cache", "[]") ?: "[]"
        val type = object : TypeToken<List<SchoolRecord>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    private fun saveLocalSchools(schools: List<SchoolRecord>) {
        prefs.edit().putString("schools_cache", gson.toJson(schools)).apply()
    }
    
    private fun loadLocalReports(): List<ReportRecord> {
        val json = prefs.getString("reports", "[]") ?: "[]"
        val type = object : TypeToken<List<ReportRecord>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    // ============ GENERAR CÓDIGOS ============
    
    fun generateCode(prefix: String): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val suffix = (1..6).map { chars[Random().nextInt(chars.length)] }.joinToString("")
        return "$prefix-$suffix"
    }
    
    // ============ INICIALIZAR ============
    
    suspend fun initialize() {
        // Asegurar que el admin existe
        val adminExists = findUserByCode("SANA-ADMIN-2025")
        if (adminExists == null) {
            saveUser(UserRecord(
                code = "SANA-ADMIN-2025",
                role = "ADMIN",
                name = "Administrador Sana",
                active = true,
                createdAt = SimpleDateFormat("yyyy-MM-dd").format(Date())
            ))
        }
    }
}
