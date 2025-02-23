package com.example.receteo

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

@HiltWorker
class RecipeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val PREFS_NAME = "RecipeNotificationPrefs"
    private val KEY_WELCOME_SENT = "welcome_sent"

    override suspend fun doWork(): Result {
        Log.d("RecipeWorker", "✅ Worker ejecutándose para enviar notificación...")

        return try {
            val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isWelcomeSent = prefs.getBoolean(KEY_WELCOME_SENT, false)

            if (!isWelcomeSent) {
                Log.d("RecipeWorker", "📢 Enviando notificación de bienvenida...")
                showWelcomeNotification()
                prefs.edit().putBoolean(KEY_WELCOME_SENT, true).apply()
            } else {
                Log.d("RecipeWorker", "📢 Enviando notificación de actualización de receta...")
                showRegularNotification(Random.nextInt(1, 3))
            }
            Log.d("RecipeWorker", "✅ Notificación enviada con éxito")
            Result.success()
        } catch (e: Exception) {
            Log.e("RecipeWorker", "❌ Error en Worker: ${e.message}")
            Result.failure()
        }
    }



    private fun showWelcomeNotification() {
        val channelId = "recipe_notifications"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recipe Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Bienvenido a Receteo!")
            .setContentText("Recibirás notificaciones sobre tus recetas y actualizaciones importantes.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showRegularNotification(notificationId: Int) {
        val channelId = "recipe_notifications"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val messages = listOf(
            "Tu nueva receta ha sido guardada con éxito! 🍲",
            "¡Has editado una receta! No olvides compartirla. 🍽️",
            "Revisa las nuevas recetas de la comunidad! 🔥",

        )

        val randomMessage = messages.random()

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📢 Receteo - Notificación")
            .setContentText(randomMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(100 + notificationId, notification)
    }
}
