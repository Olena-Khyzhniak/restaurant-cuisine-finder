package com.example.myapplication.models

// In-memory implementation of RestaurantStore.
// All data is stored in a simple ArrayList — data is lost when the app closes.
class RestaurantMemStore : RestaurantStore {

    // Internal list holding all restaurant entries
    private val restaurants = ArrayList<RestaurantModel>()

    // Counter used to assign a unique id to each new restaurant
    private var nextId: Long = 1

    // Returns a copy of the full list
    override fun findAll(): List<RestaurantModel> {
        return restaurants
    }

    // Finds and returns one restaurant by its id, or null if not found
    override fun findOne(id: Long): RestaurantModel? {
        return restaurants.find { it.id == id }
    }

    // Assigns a new id and adds the restaurant to the list
    override fun create(restaurant: RestaurantModel) {
        restaurant.id = nextId++
        restaurants.add(restaurant)
    }

    // Finds the existing entry by id and updates its fields
    override fun update(restaurant: RestaurantModel): Boolean {
        val existing = findOne(restaurant.id) ?: return false
        existing.title = restaurant.title
        existing.cuisine = restaurant.cuisine
        existing.description = restaurant.description
        return true
    }

    // Removes the restaurant with the given id from the list
    override fun delete(id: Long): Boolean {
        val restaurant = findOne(id) ?: return false
        restaurants.remove(restaurant)
        return true
    }
}
