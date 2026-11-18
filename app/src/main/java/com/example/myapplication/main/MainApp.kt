package com.example.myapplication.main

import android.app.Application
import com.example.myapplication.AppData

// Application subclass — created once when the app process starts.
// Initialises AppData so the JSON store is ready before any Activity opens.
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Load restaurants from disk (or create an empty store if first run)
        AppData.initialize(this)
    }
}
