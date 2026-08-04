package com.sana.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class UserRecord(val code: String, val role: String, val name: String, val schoolCode: String = "", val active: Boolean = true, val createdAt: String = "")
data class SchoolRecord(val code: String, val name: String, val adminCode: String, val directorName: String, val teacherCount: Int = 0, val teacherCodes: List<String> = emptyList(), val active: Boolean = true)

@Singleton
class FirebaseRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("sana_db", Context.MODE_PRIVATE)
    private val db = FirebaseDatabase.getInstance()
    
    private fun safeKey(key: String) = key.replace(".", "_").replace("-", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_")
    
    // ============ GUARDAR ============
    fun saveUser(user: UserRecord) {
        db.getReference("users").child(safeKey(user.code)).setValue(user)
        saveLocalUser(user)
    }
    
    fun saveSchool(school: SchoolRecord) {
        db.getReference("schools").child(safeKey(school.code)).setValue(school)
        saveLocalSchool(school)
    }
    
    // ============ DESCARGAR DE FIREBASE ============
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            // Descargar usuarios
            val usersSnapshot = getSnapshot("users")
            usersSnapshot?.children?.forEach { child ->
                try {
                    val map = child.value as? Map<*, *>
                    if (map != null) {
                        val user = UserRecord(
                            code = map["code"] as? String ?: "",
                            role = map["role"] as? String ?: "",
                            name = map["name"] as? String ?: "",
                            schoolCode = map["schoolCode"] as? String ?: "",
                            active = map["active"] as? Boolean ?: true,
                            createdAt = map["createdAt"] as? String ?: ""
                        )
                        if (user.code.isNotEmpty()) { saveLocalUser(user); count++ }
                    }
                } catch (_: Exception) { }
            }
            
            // Descargar escuelas
            val schoolsSnapshot = getSnapshot("schools")
            schoolsSnapshot?.children?.forEach { child ->
                try {
                    val map = child.value as? Map<*, *>
                    if (map != null) {
                        val school = SchoolRecord(
                            code = map["code"] as? String ?: "",
                            name = map["name"] as? String ?: "",
                            adminCode = map["adminCode"] as? String ?: "",
                            directorName = map["directorName"] as? String ?: "",
                            teacherCount = (map["teacherCount"] as? Long)?.toInt() ?: 0,
                            teacherCodes = (map["teacherCodes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                            active = map["active"] as? Boolean ?: true
                        )
                        if (school.code.isNotEmpty()) { saveLocalSchool(school); count++ }
                    }
                } catch (_: Exception) { }
            }
        } catch (e: Exception) { e.printStackTrace() }
        count
    }
    
    private suspend fun getSnapshot(path: String): DataSnapshot? {
        return suspendCancellableCoroutine { cont ->
            db.getReference(path).get().addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { cont.resume(null) {} }
        }
    }
    
    // ============ LOCALES ============
    fun getAllUsers(): List<UserRecord> {
        val json = prefs.getString("users", "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<UserRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun getAllSchools(): List<SchoolRecord> {
        val json = prefs.getString("schools", "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<SchoolRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun findUserByCode(code: String): UserRecord? = getAllUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    
    private fun saveLocalUser(user: UserRecord) {
        val users = getAllUsers().toMutableList()
        users.removeAll { it.code == user.code }; users.add(user)
        prefs.edit().putString("users", gson.toJson(users)).apply()
    }
    
    private fun saveLocalSchool(school: SchoolRecord) {
        val schools = getAllSchools().toMutableList()
        schools.removeAll { it.code == school.code }; schools.add(school)
        prefs.edit().putString("schools", gson.toJson(schools)).apply()
    }
    
    fun deactivateUser(code: String) {
        val users = getAllUsers().toMutableList()
        val i = users.indexOfFirst { it.code == code }
        if (i >= 0) { users[i] = users[i].copy(active = false); prefs.edit().putString("users", gson.toJson(users)).apply(); db.getReference("users").child(safeKey(code)).child("active").setValue(false) }
    }
    
    fun generateCode(prefix: String): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "$prefix-${(1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")}"
    }
    
    fun initialize() {
        if (findUserByCode("SANA-ADMIN-2025") == null) {
            saveUser(UserRecord(code = "SANA-ADMIN-2025", role = "ADMIN", name = "Administrador Sana"))
        }
    }
}
