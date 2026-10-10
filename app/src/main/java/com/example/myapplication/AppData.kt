package com.example.myapplication

import com.example.myapplication.models.RestaurantMemStore

// Global singleton providing a single shared instance of the restaurant data store.
// All activities access the same data through AppData.restaurants.
object AppData {
    val restaurants = RestaurantMemStore()
}
