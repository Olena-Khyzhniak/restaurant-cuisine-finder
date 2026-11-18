package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.RestaurantModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

// Displays all saved restaurants as markers on a full-screen OSM map.
// Tapping a marker shows the restaurant's photo, name and cuisine below the map.
class MapListActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Required by osmdroid — identifies the app to the tile server
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_list)

        // Enable Up/Back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialise views
        mapView    = findViewById(R.id.mapList)
        txtTitle   = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace   = findViewById(R.id.mapImageView)

        // Configure the OSM map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Show OSM copyright attribution overlay
        mapView.overlays.add(CopyrightOverlay(this))

        // Load all restaurant markers
        loadMarkers()
    }

    // Adds a marker for every saved restaurant and centres the map on the first one
    private fun loadMarkers() {
        val restaurants = AppData.restaurants.findAll()

        restaurants.forEach { restaurant ->
            val position = GeoPoint(restaurant.lat, restaurant.lng)
            val marker = Marker(mapView)

            marker.position = position
            marker.title    = restaurant.title
            marker.snippet  = restaurant.cuisine  // shown in the default info bubble

            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // Tapping a marker updates the info panel below the map
            marker.setOnMarkerClickListener { _, _ ->
                displayRestaurant(restaurant)
                true  // consume the click
            }

            mapView.overlays.add(marker)
        }

        // Centre the map on the first restaurant, or default to Waterford
        restaurants.firstOrNull()?.let { first ->
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(first.lat, first.lng))
        } ?: run {
            mapView.controller.setZoom(10.0)
            mapView.controller.setCenter(GeoPoint(52.245954, -7.139539))
        }

        mapView.invalidate()
    }

    // Updates the title, description and image panel when a marker is tapped
    private fun displayRestaurant(restaurant: RestaurantModel) {
        txtTitle.text       = restaurant.title
        txtDescription.text = restaurant.cuisine + if (restaurant.description.isNotBlank())
            " · ${restaurant.description}" else ""

        if (restaurant.imageUri != null) {
            imgPlace.setImageURI(Uri.parse(restaurant.imageUri))
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}
