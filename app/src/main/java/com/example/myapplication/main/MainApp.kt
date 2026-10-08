package com.example.myapplication.main

import android.app.Application
import com.example.myapplication.models.RestaurantMemStore
import com.example.myapplication.models.RestaurantStore

// Application subclass — created once when the app starts.
// Holds a single global instance of the data store
// so all activities share the same data.
class MainApp : Application() {

    // The global store instance accessible from anywhere in the app
    lateinit var restaurants: RestaurantStore

    override fun onCreate() {
        super.onCreate()
        // Initialise the in-memory store on app startup
        restaurants = RestaurantMemStore()
    }
}
