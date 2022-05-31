package pl.edu.pja.pro_2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import pl.edu.pja.pro_2.repository.LocationRepository
import pl.edu.pja.pro_2.repository.SharedPrefsLocationRepository


const val Range = 50.0
private const val STROKE_WIDTH = 10f

class MapFragment : Fragment() {


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationRepository: LocationRepository
    private lateinit var map: GoogleMap

    @RequiresApi(Build.VERSION_CODES.O)
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        locationRepository.savedLocation?.let {
            drawCircle(it)
        }
        turnOnLocation()
        locationRepository.savedLocation?.let {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 16f
                )
            )
            drawCircle(it)
        }
        googleMap.setOnMapClickListener(::onMapClick)
    }


    @SuppressLint("MissingPermission")
    private val onPermissionResult = { _: Map<String, Boolean> ->
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

        }
    }


    private fun turnOnLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onMapClick(latLng: LatLng) {
        drawCircle(latLng)
        askForSave(latLng)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ShowToast")
    private fun askForSave(latLng: LatLng) {
        Snackbar.make(
            requireView(),
            "save location?",
            Snackbar.LENGTH_INDEFINITE
        ).setAction(
            "save"
        ) {
            save(latLng)
        }.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save(latLng: LatLng) {
        locationRepository.savedLocation = latLng
        Geofencing.createGeoFence(requireContext(), latLng)
    }

    private fun drawCircle(latLng: LatLng) {
        val circle = CircleOptions()
            .strokeColor(Color.RED)
            .radius(Range)
            .center(latLng)
            .strokeWidth(STROKE_WIDTH)
        val dot = CircleOptions()
            .strokeColor(Color.BLACK)
            .center(latLng)
            .strokeWidth(STROKE_WIDTH)
            .radius(1.0)
        map.apply {
            clear()
            addCircle(circle)
            addCircle(dot)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            onPermissionResult
        )
        locationRepository = SharedPrefsLocationRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = false
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}