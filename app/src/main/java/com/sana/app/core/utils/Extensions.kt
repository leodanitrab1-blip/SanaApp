package com.sana.app.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * 🌿 SANA - Funciones de Extensión
 * 
 * Utilidades y extensiones de Kotlin para simplificar
 * operaciones comunes en toda la aplicación.
 */

// ============================================
// EXTENSIONES DE STRING
// ============================================

/**
 * Calcula el hash SHA-256 de un string
 * Usado para almacenar contraseñas de forma segura
 */
fun String.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(this.toByteArray(Charsets.UTF_8))
    return hash.joinToString("") { "%02x".format(it) }
}

/**
 * Verifica si un string es un email válido
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    return this.matches(emailRegex) && this.length <= 254
}

/**
 * Verifica si un string es un número de teléfono válido (formato internacional)
 */
fun String.isValidPhone(): Boolean {
    val phoneRegex = Regex("^\\+?[0-9]{8,15}$")
    return this.matches(phoneRegex)
}

/**
 * Trunca un string a una longitud máxima añadiendo "..." si es necesario
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length <= maxLength) this
    else this.substring(0, maxLength - 3) + "..."
}

/**
 * Capitaliza la primera letra de cada palabra
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

/**
 * Oculta parcialmente un texto (ej: email -> j***@gmail.com)
 */
fun String.maskSensitive(): String {
    return when {
        this.contains("@") -> {
            val parts = this.split("@")
            "${parts[0].take(1)}***@${parts.getOrElse(1) { "" }}"
        }
        this.length > 4 -> "${this.take(2)}${"*".repeat(this.length - 4)}${this.takeLast(2)}"
        else -> "***"
    }
}

// ============================================
// EXTENSIONES DE CONTEXT
// ============================================

/**
 * Abre una URL en el navegador
 */
fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        android.util.Log.e("Extensions", "Error al abrir URL: $url", e)
    }
}

/**
 * Abre el marcador con un número de teléfono
 */
fun Context.dialPhone(phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        android.util.Log.e("Extensions", "Error al marcar: $phone", e)
    }
}

/**
 * Abre la app de email con un destinatario predefinido
 */
fun Context.sendEmail(to: String, subject: String = "", body: String = "") {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$to")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    } catch (e: Exception) {
        android.util.Log.e("Extensions", "Error al enviar email a: $to", e)
    }
}

/**
 * Comparte texto con otras aplicaciones
 */
fun Context.shareText(text: String, title: String = "Compartir") {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(Intent.createChooser(intent, title))
    } catch (e: Exception) {
        android.util.Log.e("Extensions", "Error al compartir", e)
    }
}

// ============================================
// EXTENSIONES DE LONG (TIMESTAMP)
// ============================================

/**
 * Convierte un timestamp (epoch millis) a fecha formateada
 */
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Convierte un timestamp a fecha corta (sin hora)
 */
fun Long.toShortDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Convierte un timestamp a hora formateada
 */
fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Devuelve "Hoy", "Ayer" o la fecha según corresponda
 */
fun Long.toRelativeDate(): String {
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@toRelativeDate }
    
    return when {
        now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) -> "Hoy"
        
        now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR) == 1 -> "Ayer"
        
        else -> this.toShortDate()
    }
}

/**
 * Calcula tiempo transcurrido en formato legible
 */
fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < 60_000 -> "Ahora mismo"
        diff < 3_600_000 -> "${diff / 60_000} min"
        diff < 86_400_000 -> "${diff / 3_600_000} h"
        diff < 604_800_000 -> "${diff / 86_400_000} d"
        diff < 2_592_000_000 -> "${diff / 604_800_000} sem"
        else -> this.toShortDate()
    }
}

// ============================================
// EXTENSIONES DE LIST
// ============================================

/**
 * Alternativa segura a getOrElse con índice
 */
fun <T> List<T>.safeGet(index: Int): T? {
    return if (index in 0 until size) this[index] else null
}

/**
 * Agrupa elementos por una clave y cuenta ocurrencias
 */
fun <T, K> List<T>.countBy(keySelector: (T) -> K): Map<K, Int> {
    return this.groupBy(keySelector).mapValues { it.value.size }
}