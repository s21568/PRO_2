package pl.edu.pja.pro_2.repository

import android.content.Context
import com.google.android.gms.maps.model.LatLng

private const val FILENAME = "settings"
private const val KEY_LAT = "lat"
private const val KEY_LNG = "lng"
private const val NO_VALUE = Float.NaN

class SharedPrefsLocationRepository(context: Context) : LocationRepository {

    private val sharedPrefs =
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)


    override var savedLocation: LatLng?
        get() {
            val lat = sharedPrefs.getFloat(KEY_LAT, NO_VALUE)
            val lng = sharedPrefs.getFloat(KEY_LNG, NO_VALUE)
            if (lat == NO_VALUE || lng == NO_VALUE) {
                return null
            } else {
                return LatLng(lat.toDouble(), lng.toDouble())
            }
        }
        set(value) {
            sharedPrefs.edit()
                .putFloat(KEY_LAT, value?.latitude?.toFloat() ?: NO_VALUE)
                .putFloat(KEY_LNG, value?.longitude?.toFloat() ?: NO_VALUE)
                .apply()
        }

}