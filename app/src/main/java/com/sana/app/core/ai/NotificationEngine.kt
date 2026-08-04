package com.sana.app.core.ai

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

/**
 * 🔔 Notification Engine
 * Envía notificaciones motivadoras personalizadas en segundo plano
 */
class NotificationEngine(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "sana_motivation"
        const val NOTIFICATION_ID = 1001
    }
    
    private val morningMessages = listOf(
        "☀️ Buenos días. Hoy es un nuevo comienzo. Respira profundo y sonríe.",
        "🌅 Cada amanecer trae una nueva oportunidad. ¡Aprovecha este día!",
        "💪 Levántate con fuerza. Eres más valiente de lo que crees.",
        "🌟 Hoy puede ser un gran día. Solo cree en ti."
    )
    
    private val afternoonMessages = listOf(
        "🌤️ ¿Cómo va tu día? Recuerda hidratarte y tomar un respiro.",
        "🌸 Pausa. Respira. Todo estará bien.",
        "💭 No estás solo/a. Alguien en el mundo está pensando en ti.",
        "🦋 Las pequeñas pausas hacen grandes diferencias."
    )
    
    private val eveningMessages = listOf(
        "🌙 Termina el día con gratitud. Mañana será mejor.",
        "✨ Hiciste lo mejor que pudiste hoy. Eso es suficiente.",
        "💤 Descansa. Tu mente y cuerpo te lo agradecerán.",
        "🌟 Mañana es una nueva oportunidad. Dulces sueños."
    )
    
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Mensajes Motivadores",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Recordatorios de bienestar" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    
    fun scheduleMotivationalNotifications(profile: UserProfile) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val name = profile.preferredName.ifEmpty { "amigo" }
        
        // Programar notificaciones cada 4 horas
        scheduleNotification(alarmManager, 8, morningMessages.random().replace("amigo", name))
        scheduleNotification(alarmManager, 12, afternoonMessages.random().replace("amigo", name))
        scheduleNotification(alarmManager, 20, eveningMessages.random().replace("amigo", name))
    }
    
    private fun scheduleNotification(alarmManager: AlarmManager, hour: Int, message: String) {
        val intent = Intent(context, MotivationReceiver::class.java).apply { putExtra("message", message) }
        val pendingIntent = PendingIntent.getBroadcast(context, hour, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, Random().nextInt(30))
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }
        
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }
}

class MotivationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "🌟 Recuerda: eres importante."
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, NotificationEngine.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🌿 Sana")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        manager.notify(NotificationEngine.NOTIFICATION_ID, notification)
    }
}
