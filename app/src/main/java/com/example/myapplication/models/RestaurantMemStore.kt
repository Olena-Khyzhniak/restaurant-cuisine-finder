package com.example.myapplication.models

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.concurrent.atomic.AtomicLong

// JSON-backed implementation of RestaurantStore.
// Data is loaded from a JSON file on creation and saved after every modification.
// The file lives in the app's private files directory - no extra permissions needed.
class RestaurantMemStore(private val file: File) : RestaurantStore {

    // Internal list holding all restaurant entries
    private val restaurants = ArrayList<RestaurantModel>()

    // Thread-safe counter used to assign a unique id to each new restaurant
    private val lastId = AtomicLong(0L)

    // Gson instance with pretty-printing for readable JSON files
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    // Reads the JSON file and populates the list; resets the id counter
    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<RestaurantModel>>() {}.type
        val loaded: ArrayList<RestaurantModel> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        restaurants.clear()
        restaurants.addAll(loaded)

        // Resume id counter from the highest id already in the file
        lastId.set(restaurants.maxOfOrNull { it.id } ?: 0L)
    }

    // Serialises the list to the JSON file
    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(restaurants))
    }

    // Returns the full list
    override fun findAll(): List<RestaurantModel> = restaurants

    // Finds and returns one restaurant by its id, or null if not found
    override fun findOne(id: Long): RestaurantModel? =
        restaurants.find { it.id == id }

    // Validates, assigns a new id, adds to the list and persists
    override fun create(restaurant: RestaurantModel) {
        restaurant.validate()
        restaurant.id = lastId.incrementAndGet()
        restaurants.add(restaurant)
        save()
    }

    // Validates, updates each field of the existing entry and persists
    override fun update(restaurant: RestaurantModel): Boolean {
        restaurant.validate()
        val existing = findOne(restaurant.id) ?: return false
        existing.title       = restaurant.title
        existing.cuisine     = restaurant.cuisine
        existing.description = restaurant.description
        existing.rating      = restaurant.rating
        existing.imageUri    = restaurant.imageUri
        existing.lat         = restaurant.lat
        existing.lng         = restaurant.lng
        save()
        return true
    }

    // Removes the restaurant with the given id and persists
    override fun delete(id: Long): Boolean {
        val restaurant = findOne(id) ?: return false
        restaurants.remove(restaurant)
        save()
        return true
    }
}
