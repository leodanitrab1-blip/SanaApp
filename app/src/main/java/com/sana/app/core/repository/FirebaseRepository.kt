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
data class GuideRecord(val title: String, val subject: String, val content: String, val authorCode: String, val visibility: String = "PUBLIC", val createdAt: String = "")
data class DiaryRecord(val userId: String, val mood: String, val title: String, val content: String, val date: String)
data class LogbookRecord(val teacherCode: String, val studentName: String, val observation: String, val category: String, val mood: String, val date: String)
data class AnnouncementRecord(val schoolCode: String, val title: String, val content: String, val priority: String, val date: String)
data class ParentRecord(val code: String, val name: String, val studentName: String, val teacherCode: String, val active: Boolean = true)

@Singleton
class FirebaseRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("sana_db", Context.MODE_PRIVATE)
    private val db = FirebaseDatabase.getInstance()
    
    private fun safeKey(key: String) = key.replace(".", "_").replace("-", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_")
    
    fun save(path: String, key: String, data: Any) { db.getReference(path).child(safeKey(key)).setValue(data) }
    
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        var count = 0
        listOf("users", "schools").forEach { path ->
            try { getSnapshot(path)?.children?.forEach { c -> prefs.edit().putString("fb_${path}_${c.key}", gson.toJson(c.value)).apply(); count++ } } catch (_: Exception) { }
        }; count
    }
    
    private suspend fun getSnapshot(path: String): DataSnapshot? = suspendCancellableCoroutine { c -> db.getReference(path).get().addOnSuccessListener { c.resume(it) {} }.addOnFailureListener { c.resume(null) {} } }
    
    // ============ USUARIOS ============
    fun saveUser(user: UserRecord) { save("users", user.code, user); saveLocalUser(user) }
    fun getAllUsers(): List<UserRecord> = getLocalUsers()
    fun findUserByCode(code: String): UserRecord? = getAllUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    fun deactivateUser(code: String) { val u = getAllUsers().toMutableList(); val i = u.indexOfFirst { it.code == code }; if (i >= 0) { u[i] = u[i].copy(active = false); saveUsers(u); save("users", code, u[i]) } }
    
    private fun saveLocalUser(user: UserRecord) { val u = getAllUsers().toMutableList(); u.removeAll { it.code == user.code }; u.add(user); saveUsers(u) }
    private fun getLocalUsers(): List<UserRecord> { val j = prefs.getString("users", "[]") ?: "[]"; return try { gson.fromJson(j, object : TypeToken<List<UserRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() } }
    private fun saveUsers(users: List<UserRecord>) { prefs.edit().putString("users", gson.toJson(users)).apply() }
    
    // ============ ESCUELAS ============
    fun saveSchool(school: SchoolRecord) { save("schools", school.code, school); saveLocalSchool(school) }
    fun getAllSchools(): List<SchoolRecord> { val j = prefs.getString("schools", "[]") ?: "[]"; return try { gson.fromJson(j, object : TypeToken<List<SchoolRecord>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() } }
    private fun saveLocalSchool(school: SchoolRecord) { val s = getAllSchools().toMutableList(); s.removeAll { it.code == school.code }; s.add(school); prefs.edit().putString("schools", gson.toJson(s)).apply() }
    
    // ============ JUEGOS ============
    fun saveGame(game: GameRecord) { save("games", game.fileName, game) }
    
    // ============ OTROS ============
    fun saveParent(parent: ParentRecord) { save("parents", parent.code, parent) }
    fun saveDiary(entry: DiaryRecord) { save("diary", entry.userId + "_" + entry.date, entry) }
    fun saveLogbook(entry: LogbookRecord) { save("logbook", entry.teacherCode + "_" + entry.date, entry) }
    fun saveAnnouncement(ann: AnnouncementRecord) { save("announcements", ann.schoolCode + "_" + ann.date, ann) }
    fun saveGuide(guide: GuideRecord) { save("guides", guide.title, guide) }
    
    fun generateCode(prefix: String): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "$prefix-${(1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")}"
    }
    
    fun initialize() { if (findUserByCode("SANA-ADMIN-2025") == null) saveUser(UserRecord(code = "SANA-ADMIN-2025", role = "ADMIN", name = "Administrador Sana")) }
}
