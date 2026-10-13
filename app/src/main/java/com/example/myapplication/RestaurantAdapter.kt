package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.myapplication.models.RestaurantModel

// Adapter bridges the restaurant data list and the RecyclerView.
// For each item it inflates card_restaurant.xml and binds the data.
class RestaurantAdapter(
    private var restaurants: List<RestaurantModel>,
    private val onEdit: (RestaurantModel) -> Unit,
    private val onDelete: (RestaurantModel) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    // ViewHolder caches the views inside one card so they don't need to be
    // looked up on every scroll — improves list performance.
    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView        = itemView.findViewById(R.id.restaurantTitle)
        val cuisine: TextView      = itemView.findViewById(R.id.restaurantCuisine)
        val description: TextView  = itemView.findViewById(R.id.restaurantDescription)
        val rating: RatingBar      = itemView.findViewById(R.id.restaurantRating)
        val btnEdit: MaterialButton   = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    // Inflates card_restaurant.xml and wraps it in a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    // Fills the card views with data from the restaurant at the given position
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.title.text       = restaurant.title
        holder.cuisine.text     = restaurant.cuisine
        holder.description.text = restaurant.description
        holder.rating.rating    = restaurant.rating
        holder.btnEdit.setOnClickListener   { onEdit(restaurant) }
        holder.btnDelete.setOnClickListener { onDelete(restaurant) }
    }

    override fun getItemCount(): Int = restaurants.size

    // Replaces the data set and refreshes the list
    fun updateData(newList: List<RestaurantModel>) {
        restaurants = newList
        notifyDataSetChanged()
    }
}
