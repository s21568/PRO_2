package pl.edu.pja.pro_2

import android.annotation.SuppressLint
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
private val wishItemId=0

object Notifications {


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context) {
        val channel =
            NotificationChannel(CHANNEL_ID, "WishLista", NotificationManager.IMPORTANCE_LOW)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(name:String,context: Context) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Przypomnienie o produkcie: $name")
        .addAction(0, "Nie dzis.", createIntent(context))
        .addAction(0, "Kupuje!", createIntentEdit(context))
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createIntentEdit(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context, 1,
            Intent(context, MainActivity::class.java)
                .putExtra("id", 1), PendingIntent.FLAG_UPDATE_CURRENT
        )
}