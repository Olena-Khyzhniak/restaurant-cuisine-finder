package com.example.myapplication.models

// Data class representing a single restaurant entry.
// 'id' is auto-assigned by the store — not entered by the user.
data class RestaurantModel(
    var id: Long = 0,
    var title: String = "",
    var cuisine: String = "",
    var description: String = ""
)
