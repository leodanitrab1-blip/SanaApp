package com.sana.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

data class UserRecord(
    val code: String,
    val role: String,
    val name: String,
    val schoolCode: String = "",
    val active: Boolean = true,
    val createdAt: String = ""
)

data class SchoolRecord(
    val code: String,
    val name: String,
    val adminCode: String,
    val directorName: String,
    val active: Boolean = true
)

@Singleton
class DataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences("sana_db", Context.MODE_PRIVATE)
    
    // ============ USUARIOS ============
    
    fun getAllUsers(): List<UserRecord> {
        val json = prefs.getString("users", "[]") ?: "[]"
        val type = object : TypeToken<List<UserRecord>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun saveUser(user: UserRecord) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.code == user.code }
        users.add(user)
        saveUsers(users)
    }
    
    fun findUserByCode(code: String): UserRecord? {
        return getAllUsers().find { it.code == code && it.active }
    }
    
    fun deactivateUser(code: String): Boolean {
        val users = getAllUsers().toMutableList()
        val index = users.indexOfFirst { it.code == code }
        if (index >= 0) {
            users[index] = users[index].copy(active = false)
            saveUsers(users)
            return true
        }
        return false
    }
    
    private fun saveUsers(users: List<UserRecord>) {
        prefs.edit().putString("users", gson.toJson(users)).apply()
    }
    
    // ============ ESCUELAS ============
    
    fun getAllSchools(): List<SchoolRecord> {
        val json = prefs.getString("schools", "[]") ?: "[]"
        val type = object : TypeToken<List<SchoolRecord>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun saveSchool(school: SchoolRecord) {
        val schools = getAllSchools().toMutableList()
        schools.removeAll { it.code == school.code }
        schools.add(school)
        prefs.edit().putString("schools", gson.toJson(schools)).apply()
    }
    
    // ============ GENERAR CÓDIGOS ============
    
    fun generateCode(prefix: String): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val suffix = (1..6).map { chars[Random().nextInt(chars.length)] }.joinToString("")
        return "$prefix-$suffix"
    }
    
    // ============ INICIALIZAR ============
    
    fun initialize() {
        // Crear admin si no existe
        if (findUserByCode("SANA-ADMIN-2025") == null) {
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
