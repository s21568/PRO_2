package pl.edu.pja.pro_2

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import pl.edu.pja.pro_2.service.AlertService

private const val REQUEST_CODE = 1

object Geofencing {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createGeoFence(context: Context, latLng: LatLng) {
        Log.d("xd","dodano $latLng")
        val geofence = Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, Range.toFloat())
            .setRequestId("home")
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).let {
                context.startActivity(it)
            }
        } else {
            LocationServices.getGeofencingClient(context)
                .addGeofences(request, makePendingIntentAlert(context))
                .addOnFailureListener { println(it) }
                .addOnSuccessListener { println("dodano") }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun makePendingIntentAlert(context: Context): PendingIntent =
        PendingIntent.getForegroundService(
            context,
            REQUEST_CODE,
            Intent(context, AlertService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}