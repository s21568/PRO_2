package pl.edu.pja.pro_2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.edu.pja.pro_2.service.AlertService

private const val CHANNEL_ID = "ID_CHANNEL_DEFAULT"
private const val REQUEST_CODE = 10

object Notifications {


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context) {
        val channel =
            NotificationChannel(CHANNEL_ID, "WishLista", NotificationManager.IMPORTANCE_LOW)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(context: Context) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Przypomnienie o produkcie")
        .addAction(0, "nie dzis", createIntent(context))
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createIntent(context: Context): PendingIntent =
        PendingIntent.getForegroundService(
            context,
            REQUEST_CODE,
            Intent(context, AlertService::class.java)
                .putExtra("check", true),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}