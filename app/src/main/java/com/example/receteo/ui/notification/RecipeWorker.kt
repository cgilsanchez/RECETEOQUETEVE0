package com.example.receteo.ui.notification

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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

            val actionType = inputData.getString("action_type") ?: ""

            when {
                !isWelcomeSent && actionType.isEmpty() -> {
                    Log.d("RecipeWorker", "📢 Enviando notificación de bienvenida...")
                    showWelcomeNotification()
                    prefs.edit().putBoolean(KEY_WELCOME_SENT, true).apply()
                }

                actionType == "create" -> {
                    Log.d("RecipeWorker", "📢 Enviando notificación de creación de receta...")
                    showNotification("Tu nueva receta ha sido guardada con éxito! 🍲")
                }

                actionType == "update" -> {
                    Log.d("RecipeWorker", "📢 Enviando notificación de actualización de receta...")
                    showNotification("¡Has editado una receta! No olvides compartirla. 🍽️")
                }

                else -> {
                    Log.d("RecipeWorker", "📢 No se envió ninguna notificación")
                }
            }

            Log.d("RecipeWorker", "✅ Notificación enviada con éxito")
            Result.success()
        } catch (e: Exception) {
            Log.e("RecipeWorker", "❌ Error en Worker: ${e.message}")
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val channelId = "recipe_notifications"
        val notificationId = 1

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Receteo en ejecución")
            .setContentText("Procesando notificación de recetas...")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        return ForegroundInfo(notificationId, notification)
    }

    private fun showWelcomeNotification() {
        showNotification("¡Bienvenido a Receteo! Recibirás notificaciones sobre tus recetas y actualizaciones importantes.")
    }

    private fun showNotification(message: String) {
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
            .setContentTitle("📢 Receteo")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
