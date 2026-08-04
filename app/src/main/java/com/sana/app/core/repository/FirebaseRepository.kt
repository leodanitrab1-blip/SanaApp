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
    
    fun <T> save(path: String, key: String, data: T) { db.getReference(path).child(safeKey(key)).setValue(data) }
    
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        var count = 0
        listOf("users", "schools", "games", "guides", "diary", "logbook", "announcements", "parents").forEach { path ->
            try { getSnapshot(path)?.children?.forEach { c -> prefs.edit().putString("fb_${path}_${c.key}", gson.toJson(c.value)).apply(); count++ } } catch (_: Exception) { }
        }; count
    }
    
    private suspend fun getSnapshot(path: String): DataSnapshot? = suspendCancellableCoroutine { c -> db.getReference(path).get().addOnSuccessListener { c.resume(it) {} }.addOnFailureListener { c.resume(null) {} } }
    
    fun saveUser(user: UserRecord) { save("users", user.code, user); saveLocal("users", user) }
    fun getAllUsers(): List<UserRecord> = getLocal("users")
    fun findUserByCode(code: String): UserRecord? = getAllUsers().find { it.code.equals(code, ignoreCase = true) && it.active }
    
    fun saveSchool(school: SchoolRecord) { save("schools", school.code, school); saveLocal("schools", school) }
    fun getAllSchools(): List<SchoolRecord> = getLocal("schools")
    
    fun saveGame(game: GameRecord) { save("games", game.fileName, game); saveLocal("games", game) }
    fun getAllGames(): List<GameRecord> = getLocal("games")
    
    fun saveGuide(guide: GuideRecord) { save("guides", guide.title, guide); saveLocal("guides", guide) }
    fun getAllGuides(): List<GuideRecord> = getLocal("guides")
    
    fun saveDiary(entry: DiaryRecord) { save("diary", entry.userId + "_" + entry.date, entry); saveLocal("diary", entry) }
    fun saveLogbook(entry: LogbookRecord) { save("logbook", entry.teacherCode + "_" + entry.date, entry); saveLocal("logbook", entry) }
    fun saveAnnouncement(ann: AnnouncementRecord) { save("announcements", ann.schoolCode + "_" + ann.date, ann); saveLocal("announcements", ann) }
    fun saveParent(parent: ParentRecord) { save("parents", parent.code, parent); saveLocal("parents", parent) }
    fun getAllParents(): List<ParentRecord> = getLocal("parents")
    
    private inline fun <reified T> saveLocal(key: String, item: T) {
        val list: MutableList<T> = getLocal(key).toMutableList(); list.add(item)
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }
    
    private inline fun <reified T> getLocal(key: String): List<T> {
        val json = prefs.getString(key, "[]") ?: "[]"
        return try { gson.fromJson(json, object : TypeToken<List<T>>() {}.type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    
    fun deactivateUser(code: String) { val users = getAllUsers().toMutableList(); val i = users.indexOfFirst { it.code == code }; if (i >= 0) { users[i] = users[i].copy(active = false); prefs.edit().putString("users", gson.toJson(users)).apply(); save("users", code, users[i]) } }
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
