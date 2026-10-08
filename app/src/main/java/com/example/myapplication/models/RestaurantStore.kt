package com.example.myapplication.models

// Interface defining the CRUD contract for any restaurant data store.
// Any storage implementation (memory, file, database) must implement this.
interface RestaurantStore {

    // Returns all restaurants as a list
    fun findAll(): List<RestaurantModel>

    // Returns a single restaurant by id, or null if not found
    fun findOne(id: Long): RestaurantModel?

    // Adds a new restaurant to the store
    fun create(restaurant: RestaurantModel)

    // Updates an existing restaurant, returns true if successful
    fun update(restaurant: RestaurantModel): Boolean

    // Deletes a restaurant by id, returns true if successful
    fun delete(id: Long): Boolean
}
