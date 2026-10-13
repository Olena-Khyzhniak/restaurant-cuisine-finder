package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import com.example.myapplication.models.RestaurantModel

// Screen for adding a new restaurant or editing an existing one.
// Launched with an "id" extra to edit; without it to create a new entry.
// Uses an ExposedDropdownMenu for cuisine (DropDownList) and a RatingBar
// instead of a plain NumberPicker-style field.
class AddEditActivity : AppCompatActivity() {

    private lateinit var inputTitle: TextInputEditText
    private lateinit var inputCuisine: AutoCompleteTextView
    private lateinit var inputDescription: TextInputEditText
    private lateinit var ratingBar: RatingBar

    // Holds the id of the restaurant being edited, or null when adding new
    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        // Enable the Up/Back arrow in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inputTitle       = findViewById(R.id.inputTitle)
        inputCuisine     = findViewById(R.id.inputCuisine)
        inputDescription = findViewById(R.id.inputDescription)
        ratingBar        = findViewById(R.id.ratingBar)

        // Populate cuisine drop-down from the string-array defined in strings.xml
        val cuisines = resources.getStringArray(R.array.cuisine_types)
        val cuisineAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cuisines)
        inputCuisine.setAdapter(cuisineAdapter)

        // Read the id passed from RestaurantListActivity (Edit button)
        val id = intent.getLongExtra("id", -1L)
        editingId = if (id != -1L) id else null

        if (editingId != null) {
            loadExistingRestaurant(editingId!!)
        }

        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { saveRestaurant() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }
    }

    // The Up arrow in the ActionBar should behave like the Back button here
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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
        inputCuisine.setText(restaurant.cuisine, false) // 'false' prevents filtering
        inputDescription.setText(restaurant.description)
        ratingBar.rating = restaurant.rating
    }

    // Validates the form and creates or updates the restaurant in AppData
    private fun saveRestaurant() {
        val title       = inputTitle.text.toString().trim()
        val cuisine     = inputCuisine.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val rating      = ratingBar.rating

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
                RestaurantModel(title = title, cuisine = cuisine,
                                description = description, rating = rating)
            )
            Toast.makeText(this, "Restaurant added!", Toast.LENGTH_SHORT).show()
        } else {
            // id present — update the existing restaurant
            AppData.restaurants.update(
                RestaurantModel(id = editingId!!, title = title, cuisine = cuisine,
                                description = description, rating = rating)
            )
            Toast.makeText(this, "Restaurant updated!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
