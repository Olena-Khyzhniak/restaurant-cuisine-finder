package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    // Holds the URI of the image chosen by the user
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        // Enable the Up/Back arrow in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inputTitle       = findViewById(R.id.inputTitle)
        inputCuisine     = findViewById(R.id.inputCuisine)
        inputDescription = findViewById(R.id.inputDescription)
        ratingBar        = findViewById(R.id.ratingBar)

        // ── Image picker ─────────────────────────────────────────────────────
        val imagePreview = findViewById<ImageView>(R.id.imageViewAdd)

        // Restore image if editing an existing restaurant
        if (editingId != null) {
            val existing = AppData.restaurants.findOne(editingId!!)
            selectedImageUri = existing?.imageUri?.let { Uri.parse(it) }
            if (selectedImageUri != null) {
                imagePreview.setImageURI(selectedImageUri)
            } else {
                imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        val imagePicker =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    // Keep persistent read access to the URI across app restarts
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // Some providers may not grant persistable access
                    }
                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                }
            }
        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

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

    // Builds a RestaurantModel from the form, calls validate(), then saves.
    // IllegalArgumentException from validate() is caught and shown as a Toast.
    private fun saveRestaurant() {
        try {
            val restaurant = RestaurantModel(
                title       = inputTitle.text.toString().trim(),
                cuisine     = inputCuisine.text.toString().trim(),
                description = inputDescription.text.toString().trim(),
                rating      = ratingBar.rating,
                imageUri    = selectedImageUri?.toString()
            )

            if (editingId == null) {
                // No id — create a new restaurant (validate() called inside create())
                AppData.restaurants.create(restaurant)
                Toast.makeText(this, "Restaurant added!", Toast.LENGTH_SHORT).show()
            } else {
                // id present — update the existing restaurant
                restaurant.id = editingId!!
                AppData.restaurants.update(restaurant)
                Toast.makeText(this, "Restaurant updated!", Toast.LENGTH_SHORT).show()
            }

            finish()

        } catch (e: IllegalArgumentException) {
            // validate() throws this — show the message to the user
            Toast.makeText(
                this,
                e.message ?: "Please check the entered values.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
