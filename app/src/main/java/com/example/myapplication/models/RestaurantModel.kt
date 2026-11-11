package com.example.myapplication.models

// Data class representing a single restaurant entry.
// 'id' is auto-assigned by the store — not entered by the user.
// 'rating' stores the 1–5 star value selected on the RatingBar.
// 'imageUri' stores the URI string of the chosen image, or null if none picked.
data class RestaurantModel(
    var id: Long = 0,
    var title: String = "",
    var cuisine: String = "",
    var description: String = "",
    var rating: Float = 3f,
    var imageUri: String? = null,
    var lat: Double = 52.245954,  // Latitude — defaults to Waterford, Ireland
    var lng: Double = -7.139539   // Longitude — defaults to Waterford, Ireland
) {
    // Called before saving to store — validates required fields.
    // Validation happens at save time, not in init, so the form can be
    // built while the user is still entering values.
    fun validate() {
        require(title.isNotBlank()) {
            "Restaurant name must not be empty."
        }
        require(cuisine.isNotBlank()) {
            "Cuisine type must not be empty."
        }
    }
}
