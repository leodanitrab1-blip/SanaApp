package com.sana.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FirebaseRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences("sana_db", Context.MODE_PRIVATE)
    private val db = FirebaseDatabase.getInstance()
    
    // ============ GUARDAR EN FIREBASE ============
    
    fun saveUser(user: UserRecord) {
        val key = user.code.replace(".", "_").replace("-", "_").replace("#", "_").replace("$", "_")
        db.getReference("users").child(key).setValue(user)
        saveLocalUser(user)
    }
    
    fun saveSchool(school: SchoolRecord) {
        val key = school.code.replace(".", "_").replace("-", "_").replace("#", "_").replace("$", "_")
        db.getReference("schools").child(key).setValue(school)
        saveLocalSchool(school)
    }
    
    // ============ DESCARGAR DE FIREBASE ============
    
    suspend fun syncFromFirebase(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            // Usuarios
            val usersSnapshot = getRef("users")
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
            
            // Escuelas
            val schoolsSnapshot = getRef("schools")
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
    
    private suspend fun getRef(path: String): DataSnapshot? {
        return try {
            kotlinx.coroutines.suspendCancellableCoroutine { cont ->
                db.getReference(path).get().addOnSuccessListener { cont.resume(it) {} }
                    .addOnFailureListener { cont.resume(null) {} }
            }
        } catch (e: Exception) { null }
    }
    
    // ============ BUSCAR ============
    
    fun findUserByCode(code: String): UserRecord? {
        return getAllLocalUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    }
    
    fun getAllLocalUsers(): List<UserRecord> {
        val json = prefs.getString("users", "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<UserRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun getAllLocalSchools(): List<SchoolRecord> {
        val json = prefs.getString("schools", "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<SchoolRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    private fun saveLocalUser(user: UserRecord) {
        val users = getAllLocalUsers().toMutableList()
        users.removeAll { it.code == user.code }; users.add(user)
        prefs.edit().putString("users", gson.toJson(users)).apply()
    }
    
    private fun saveLocalSchool(school: SchoolRecord) {
        val schools = getAllLocalSchools().toMutableList()
        schools.removeAll { it.code == school.code }; schools.add(school)
        prefs.edit().putString("schools", gson.toJson(schools)).apply()
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
