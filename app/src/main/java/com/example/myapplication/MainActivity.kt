package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// Main menu screen — entry point of the app.
// Shows two buttons: view the list or add a new restaurant.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigate to the restaurant list screen
        findViewById<Button>(R.id.btnViewRestaurants).setOnClickListener {
            startActivity(Intent(this, RestaurantListActivity::class.java))
        }

        // Navigate directly to the add restaurant screen
        findViewById<Button>(R.id.btnAddRestaurant).setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }
}
