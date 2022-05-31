package pl.edu.pja.pro_2.repository

import com.google.android.gms.maps.model.LatLng

interface LocationRepository {
    var savedLocation: LatLng?
}