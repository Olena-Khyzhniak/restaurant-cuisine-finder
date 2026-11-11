package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

// Full-screen OSM map that lets the user tap to pick a location.
// Returns the selected latitude and longitude back to AddEditActivity via Intent extras.
class MapPickerActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Required by osmdroid — identifies the app to the tile server
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Use the coordinates passed from AddEditActivity, or default to Waterford
        val lat = intent.getDoubleExtra("latitude", 52.245954)
        val lon = intent.getDoubleExtra("longitude", -7.139539)
        val start = GeoPoint(lat, lon)

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        // Place a marker at the starting location immediately
        setSelectedPoint(start)

        // Show all existing restaurants as markers on the map
        showExistingRestaurants()

        // Listen for single tap and long press to move the selected marker
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }

            override fun longPressHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }
        }
        map.overlays.add(MapEventsOverlay(receiver))

        // Return the selected point to AddEditActivity
        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            val point = selectedPoint ?: return@setOnClickListener
            val result = intent
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    // Places or moves the draggable selection marker
    private fun setSelectedPoint(point: GeoPoint) {
        selectedPoint = point

        if (marker == null) {
            marker = Marker(map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(marker)
        }

        marker?.position = point
        marker?.title = "Selected location"
        map.invalidate() // redraw the map
    }

    // Adds a read-only marker for every saved restaurant so the user can
    // see existing locations while picking a new one
    private fun showExistingRestaurants() {
        AppData.restaurants.findAll().forEach { restaurant ->
            val point = GeoPoint(restaurant.lat, restaurant.lng)
            val existingMarker = Marker(map).apply {
                position = point
                title    = restaurant.title
                snippet  = restaurant.cuisine  // shown in the info bubble
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(existingMarker)
        }
        map.invalidate()
    }

    // osmdroid requires onResume/onPause to manage tile loading and GPS
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }
}
