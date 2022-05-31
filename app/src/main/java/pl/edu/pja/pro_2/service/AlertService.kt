package pl.edu.pja.pro_2.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import pl.edu.pja.pro_2.Notifications
import pl.edu.pja.pro_2.R
import pl.edu.pja.pro_2.Range
import pl.edu.pja.pro_2.repository.LocationRepository
import pl.edu.pja.pro_2.repository.SharedPrefsLocationRepository

private var NOTIFICATION_ID = 1

class AlertService : Service() {

    private lateinit var locationRepository: LocationRepository
    private lateinit var locationClient: FusedLocationProviderClient

    private val callback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(loc: LocationResult) {
            locationRepository.savedLocation?.let {
                stopSelf()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRepository = SharedPrefsLocationRepository(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra("check", false) == true) {
            checkLocation()
        }
        startForeground(NOTIFICATION_ID, Notifications.createNotification(this))
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun checkLocation() {
        val req = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100
            numUpdates = 1
        }
        locationClient.requestLocationUpdates(req, callback, Looper.getMainLooper())
    }

    override fun onBind(intent: Intent): IBinder? = null
}