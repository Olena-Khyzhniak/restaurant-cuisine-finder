package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Main screen: shows the list of all restaurants with Edit and Delete buttons.
class MainActivity : AppCompatActivity() {

    // Container that holds the dynamically built list of restaurants
    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUserInterface()
    }

    // Called every time the screen becomes visible (e.g. returning from AddEditActivity).
    // Refreshes the list so any new or edited restaurant is shown immediately.
    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displayRestaurants()
        }
    }

    // Builds the entire UI programmatically and sets it as the content view.
    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Restaurants"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Restaurant"
            setOnClickListener {
                val intent = Intent(this@MainActivity, AddEditActivity::class.java)
                startActivity(intent)
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            listLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)

        displayRestaurants()
    }

    // Clears and rebuilds the restaurant list from AppData.
    private fun displayRestaurants() {

        listLayout.removeAllViews()

        val restaurants = AppData.restaurants.findAll()

        if (restaurants.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No restaurants yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            }
            listLayout.addView(emptyText)
            return
        }

        for (restaurant in restaurants) {

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val itemTitle = TextView(this).apply {
                text = "${restaurant.id}: ${restaurant.title}"
                textSize = 20f
            }

            val itemCuisine = TextView(this).apply {
                text = "Cuisine: ${restaurant.cuisine}"
                textSize = 16f
            }

            val itemDescription = TextView(this).apply {
                text = restaurant.description
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, AddEditActivity::class.java)
                    intent.putExtra("id", restaurant.id)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.restaurants.delete(restaurant.id)
                    displayRestaurants()
                }
            }

            itemLayout.addView(itemTitle)
            itemLayout.addView(itemCuisine)
            itemLayout.addView(itemDescription)
            itemLayout.addView(editButton)
            itemLayout.addView(deleteButton)

            listLayout.addView(itemLayout)
        }
    }
}
