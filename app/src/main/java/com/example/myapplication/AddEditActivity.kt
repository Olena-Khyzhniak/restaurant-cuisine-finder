package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.RestaurantModel

// Screen for adding a new restaurant or editing an existing one.
// If launched with an "id" extra, it loads that restaurant for editing.
// If launched without an "id" (or id == -1), it creates a new restaurant.
class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var cuisineInput: EditText
    private lateinit var descriptionInput: EditText

    // Holds the id of the restaurant being edited, or null when adding a new one
    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()

        // Read the id passed from MainActivity (Edit button)
        val id = intent.getLongExtra("id", -1L)
        editingId = if (id != -1L) id else null

        if (editingId != null) {
            loadExistingRestaurant(editingId!!)
        }
    }

    // Builds the form UI programmatically.
    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        titleInput = EditText(this).apply {
            hint = "Title"
        }

        cuisineInput = EditText(this).apply {
            hint = "Cuisine type"
        }

        descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { saveRestaurant() }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener { finish() }
        }

        root.addView(titleInput)
        root.addView(cuisineInput)
        root.addView(descriptionInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    // Loads an existing restaurant's data into the form fields.
    private fun loadExistingRestaurant(id: Long) {

        val restaurant = AppData.restaurants.findOne(id)

        if (restaurant == null) {
            Toast.makeText(this, "Restaurant not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        titleInput.setText(restaurant.title)
        cuisineInput.setText(restaurant.cuisine)
        descriptionInput.setText(restaurant.description)
    }

    // Reads form fields, validates input, then creates or updates the restaurant.
    private fun saveRestaurant() {

        val title = titleInput.text.toString().trim()
        val cuisine = cuisineInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        if (cuisine.isEmpty()) {
            cuisineInput.error = "Cuisine type is required"
            return
        }

        if (editingId == null) {
            // No id — create a new restaurant
            val restaurant = RestaurantModel(
                title = title,
                cuisine = cuisine,
                description = description
            )
            AppData.restaurants.create(restaurant)
            Toast.makeText(this, "Restaurant created", Toast.LENGTH_SHORT).show()

        } else {
            // id present — update the existing restaurant
            val restaurant = RestaurantModel(
                id = editingId!!,
                title = title,
                cuisine = cuisine,
                description = description
            )
            AppData.restaurants.update(restaurant)
            Toast.makeText(this, "Restaurant updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
