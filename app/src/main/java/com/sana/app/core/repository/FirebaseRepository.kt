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

// ============ MODELOS ============
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
    
    // ============ GUARDAR EN FIREBASE ============
    fun <T> save(path: String, key: String, data: T) {
        db.getReference(path).child(safeKey(key)).setValue(data)
    }
    
    // ============ SINCRONIZAR TODO ============
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        var count = 0
        val paths = listOf("users", "schools", "games", "guides", "diary", "logbook", "announcements", "parents")
        paths.forEach { path ->
            try {
                val snapshot = getSnapshot(path)
                snapshot?.children?.forEach { child ->
                    val json = gson.toJson(child.value)
                    prefs.edit().putString("fb_${path}_${child.key}", json).apply()
                    count++
                }
            } catch (_: Exception) { }
        }
        count
    }
    
    private suspend fun getSnapshot(path: String): DataSnapshot? {
        return suspendCancellableCoroutine { cont ->
            db.getReference(path).get().addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { cont.resume(null) {} }
        }
    }
    
    // ============ USUARIOS ============
    fun saveUser(user: UserRecord) { save("users", user.code, user); saveLocalList("users", user) }
    fun getAllUsers(): List<UserRecord> = getLocalList("users")
    fun findUserByCode(code: String): UserRecord? = getAllUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    
    // ============ ESCUELAS ============
    fun saveSchool(school: SchoolRecord) { save("schools", school.code, school); saveLocalList("schools", school) }
    fun getAllSchools(): List<SchoolRecord> = getLocalList("schools")
    
    // ============ JUEGOS ============
    fun saveGame(game: GameRecord) { save("games", game.fileName, game); saveLocalList("games", game) }
    fun getAllGames(): List<GameRecord> = getLocalList("games")
    
    // ============ GUÍAS ============
    fun saveGuide(guide: GuideRecord) { save("guides", guide.title, guide); saveLocalList("guides", guide) }
    fun getAllGuides(): List<GuideRecord> = getLocalList("guides")
    
    // ============ DIARIO ============
    fun saveDiary(entry: DiaryRecord) { save("diary", entry.userId + "_" + entry.date, entry); saveLocalList("diary", entry) }
    fun getDiaryForUser(userId: String): List<DiaryRecord> = getLocalList<DiaryRecord>("diary").filter { it.userId == userId }
    
    // ============ BITÁCORA ============
    fun saveLogbook(entry: LogbookRecord) { save("logbook", entry.teacherCode + "_" + entry.date, entry); saveLocalList("logbook", entry) }
    fun getLogbookForTeacher(code: String): List<LogbookRecord> = getLocalList<LogbookRecord>("logbook").filter { it.teacherCode == code }
    
    // ============ AVISOS ============
    fun saveAnnouncement(ann: AnnouncementRecord) { save("announcements", ann.schoolCode + "_" + ann.date, ann); saveLocalList("announcements", ann) }
    fun getAnnouncementsForSchool(code: String): List<AnnouncementRecord> = getLocalList<AnnouncementRecord>("announcements").filter { it.schoolCode == code }
    
    // ============ PADRES ============
    fun saveParent(parent: ParentRecord) { save("parents", parent.code, parent); saveLocalList("parents", parent) }
    fun getAllParents(): List<ParentRecord> = getLocalList("parents")
    
    // ============ LOCAL CACHE ============
    private fun <T> saveLocalList(key: String, item: T) {
        val type = object : TypeToken<MutableList<T>>() {}.type
        val list: MutableList<T> = try { gson.fromJson(prefs.getString(key, "[]"), type) ?: mutableListOf() } catch (e: Exception) { mutableListOf() }
        list.add(item)
        prefs.edit().putString(key, gson.toJson(list.distinct())).apply()
    }
    
    private inline fun <reified T> getLocalList(key: String): List<T> {
        val json = prefs.getString(key, "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<T>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    // ============ UTILIDADES ============
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
