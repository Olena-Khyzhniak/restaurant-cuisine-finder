package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.RestaurantModel

// Screen for adding a new restaurant or editing an existing one.
// Launched with an "id" extra to edit; without it to create a new entry.
class AddEditActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputCuisine: EditText
    private lateinit var inputDescription: EditText

    // Holds the id of the restaurant being edited, or null when adding new
    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        inputTitle = findViewById(R.id.inputTitle)
        inputCuisine = findViewById(R.id.inputCuisine)
        inputDescription = findViewById(R.id.inputDescription)

        // Read the id passed from RestaurantListActivity (Edit button)
        val id = intent.getLongExtra("id", -1L)
        editingId = if (id != -1L) id else null

        if (editingId != null) {
            loadExistingRestaurant(editingId!!)
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { saveRestaurant() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    // Populates the form fields with data from an existing restaurant
    private fun loadExistingRestaurant(id: Long) {
        val restaurant = AppData.restaurants.findOne(id)
        if (restaurant == null) {
            Toast.makeText(this, "Restaurant not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        inputTitle.setText(restaurant.title)
        inputCuisine.setText(restaurant.cuisine)
        inputDescription.setText(restaurant.description)
    }

    // Validates the form and creates or updates the restaurant in AppData
    private fun saveRestaurant() {
        val title = inputTitle.text.toString().trim()
        val cuisine = inputCuisine.text.toString().trim()
        val description = inputDescription.text.toString().trim()

        if (title.isEmpty()) {
            inputTitle.error = "Title is required"
            return
        }

        if (cuisine.isEmpty()) {
            inputCuisine.error = "Cuisine type is required"
            return
        }

        if (editingId == null) {
            // No id — create a new restaurant
            AppData.restaurants.create(
                RestaurantModel(title = title, cuisine = cuisine, description = description)
            )
            Toast.makeText(this, "Restaurant created", Toast.LENGTH_SHORT).show()
        } else {
            // id present — update the existing restaurant
            AppData.restaurants.update(
                RestaurantModel(id = editingId!!, title = title, cuisine = cuisine, description = description)
            )
            Toast.makeText(this, "Restaurant updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
