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
data class GameRecord(val title: String, val description: String, val category: String, val fileName: String, val uploadedBy: String = "")

@Singleton
class FirebaseRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("sana_db", Context.MODE_PRIVATE)
    private val db = FirebaseDatabase.getInstance()
    
    private fun safeKey(key: String) = key.replace(".", "_").replace("-", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_")
    
    // ============ GUARDAR ============
    fun saveUser(user: UserRecord) { db.getReference("users").child(safeKey(user.code)).setValue(user); saveLocalUser(user) }
    fun saveSchool(school: SchoolRecord) { db.getReference("schools").child(safeKey(school.code)).setValue(school); saveLocalSchool(school) }
    fun saveGame(game: GameRecord) { db.getReference("games").child(safeKey(game.fileName)).setValue(game); saveLocalGame(game) }
    
    // ============ SINCRONIZAR ============
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            // Usuarios
            getSnapshot("users")?.children?.forEach { child ->
                try { val m = child.value as? Map<*,*>; if (m != null) { val u = UserRecord(code = m["code"] as? String ?: "", role = m["role"] as? String ?: "", name = m["name"] as? String ?: "", schoolCode = m["schoolCode"] as? String ?: "", active = m["active"] as? Boolean ?: true, createdAt = m["createdAt"] as? String ?: ""); if (u.code.isNotEmpty()) { saveLocalUser(u); count++ } } } catch (_: Exception) { }
            }
            // Escuelas
            getSnapshot("schools")?.children?.forEach { child ->
                try { val m = child.value as? Map<*,*>; if (m != null) { val s = SchoolRecord(code = m["code"] as? String ?: "", name = m["name"] as? String ?: "", adminCode = m["adminCode"] as? String ?: "", directorName = m["directorName"] as? String ?: "", teacherCount = (m["teacherCount"] as? Long)?.toInt() ?: 0, teacherCodes = (m["teacherCodes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()); if (s.code.isNotEmpty()) { saveLocalSchool(s); count++ } } } catch (_: Exception) { }
            }
            // Juegos
            getSnapshot("games")?.children?.forEach { child ->
                try { val m = child.value as? Map<*,*>; if (m != null) { val g = GameRecord(title = m["title"] as? String ?: "", description = m["description"] as? String ?: "", category = m["category"] as? String ?: "", fileName = m["fileName"] as? String ?: ""); if (g.fileName.isNotEmpty()) { saveLocalGame(g); count++ } } } catch (_: Exception) { }
            }
        } catch (e: Exception) { e.printStackTrace() }
        count
    }
    
    private suspend fun getSnapshot(path: String): DataSnapshot? = suspendCancellableCoroutine { c -> db.getReference(path).get().addOnSuccessListener { c.resume(it) {} }.addOnFailureListener { c.resume(null) {} } }
    
    // ============ USUARIOS LOCALES ============
    fun getAllUsers(): List<UserRecord> { val j = prefs.getString("users", "[]") ?: "[]"; return try { gson.fromJson(j, object : TypeToken<List<UserRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() } }
    fun findUserByCode(code: String): UserRecord? = getAllUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    private fun saveLocalUser(user: UserRecord) { val u = getAllUsers().toMutableList(); u.removeAll { it.code == user.code }; u.add(user); prefs.edit().putString("users", gson.toJson(u)).apply() }
    fun deactivateUser(code: String) { val u = getAllUsers().toMutableList(); val i = u.indexOfFirst { it.code == code }; if (i >= 0) { u[i] = u[i].copy(active = false); prefs.edit().putString("users", gson.toJson(u)).apply(); db.getReference("users").child(safeKey(code)).child("active").setValue(false) } }
    
    // ============ ESCUELAS LOCALES ============
    fun getAllSchools(): List<SchoolRecord> { val j = prefs.getString("schools", "[]") ?: "[]"; return try { gson.fromJson(j, object : TypeToken<List<SchoolRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() } }
    private fun saveLocalSchool(school: SchoolRecord) { val s = getAllSchools().toMutableList(); s.removeAll { it.code == school.code }; s.add(school); prefs.edit().putString("schools", gson.toJson(s)).apply() }
    
    // ============ JUEGOS LOCALES ============
    fun getAllGames(): List<GameRecord> { val j = prefs.getString("games", "[]") ?: "[]"; return try { gson.fromJson(j, object : TypeToken<List<GameRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() } }
    private fun saveLocalGame(game: GameRecord) { val g = getAllGames().toMutableList(); g.removeAll { it.fileName == game.fileName }; g.add(game); prefs.edit().putString("games", gson.toJson(g)).apply() }
    
    fun generateCode(prefix: String): String { val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; return "$prefix-${(1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")}" }
    fun initialize() { if (findUserByCode("SANA-ADMIN-2025") == null) saveUser(UserRecord(code = "SANA-ADMIN-2025", role = "ADMIN", name = "Administrador Sana")) }
}
