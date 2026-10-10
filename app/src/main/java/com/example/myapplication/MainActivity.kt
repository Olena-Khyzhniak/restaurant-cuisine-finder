package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Entry point of the application.
// Immediately launches RestaurantListActivity and finishes itself,
// so the back button does not return to a blank screen.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For now just show a blank screen until RestaurantListActivity is ready
        // This will be updated in the next commit
    }
}
