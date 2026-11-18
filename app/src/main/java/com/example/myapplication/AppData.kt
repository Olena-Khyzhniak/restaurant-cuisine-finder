package com.example.myapplication

import android.content.Context
import com.example.myapplication.models.RestaurantMemStore
import java.io.File

// Global singleton providing a single shared instance of the restaurant data store.
// Must be initialised once via AppData.initialize(context) before first use.
// All activities access the same data through AppData.restaurants.
object AppData {

    lateinit var restaurants: RestaurantMemStore
        private set

    // Called once from MainApp.onCreate() - creates the JSON file reference
    // and loads any previously saved restaurants from disk.
    fun initialize(context: Context) {
        val file = File(context.filesDir, "restaurants.json")
        restaurants = RestaurantMemStore(file)
    }
}
