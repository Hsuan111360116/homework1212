package com.example.lab14

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 0
        private val TAIPEI_101 = LatLng(25.033611, 121.565000)
        private val TAIPEI_STATION = LatLng(25.047924, 121.517081)
        private val MID_POINT = LatLng(25.032435, 121.534905)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupEdgeToEdgeUI()
        loadMap()
    }

    private fun setupEdgeToEdgeUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                loadMap()
            } else {
                finish()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        if (hasLocationPermission()) {
            setupMap(map)
        } else {
            requestLocationPermission()
        }
    }

    private fun setupMap(map: GoogleMap) {
        map.isMyLocationEnabled = true

        // Add markers
        addMarker(map, TAIPEI_101, "台北101")
        addMarker(map, TAIPEI_STATION, "台北車站")

        // Draw polyline
        map.addPolyline(
            PolylineOptions()
                .add(TAIPEI_101, MID_POINT, TAIPEI_STATION)
                .color(Color.BLUE)
                .width(10f)
        )

        // Move camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.035, 121.54), 13f))
    }

    private fun addMarker(map: GoogleMap, position: LatLng, title: String) {
        map.addMarker(MarkerOptions().position(position).title(title).draggable(true))
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this) ?: run {
            finish() // Handle null map fragment
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation && coarseLocation
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}
