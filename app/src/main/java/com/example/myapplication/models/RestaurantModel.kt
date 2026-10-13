package com.example.myapplication.models

// Data class representing a single restaurant entry.
// 'id' is auto-assigned by the store — not entered by the user.
// 'rating' stores the 1–5 star value selected on the RatingBar.
data class RestaurantModel(
    var id: Long = 0,
    var title: String = "",
    var cuisine: String = "",
    var description: String = "",
    var rating: Float = 3f
)
