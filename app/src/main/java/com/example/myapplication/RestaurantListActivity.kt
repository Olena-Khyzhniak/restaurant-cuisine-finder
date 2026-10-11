package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Displays the full list of restaurants using a RecyclerView.
// Refreshes the list every time the screen becomes visible (onResume).
class RestaurantListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)

        recyclerView = findViewById(R.id.recyclerView)
        // LinearLayoutManager stacks cards vertically, one below the other
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestaurantAdapter(
            restaurants = AppData.restaurants.findAll(),
            onEdit = { restaurant ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("id", restaurant.id)
                startActivity(intent)
            },
            onDelete = { restaurant ->
                AppData.restaurants.delete(restaurant.id)
                refreshList()
            }
        )

        recyclerView.adapter = adapter
    }

    // Called every time the screen becomes visible — keeps the list up to date
    // after returning from AddEditActivity
    override fun onResume() {
        super.onResume()
        refreshList()
    }

    // Fetches the latest data from AppData and updates the RecyclerView
    private fun refreshList() {
        adapter.updateData(AppData.restaurants.findAll())
    }
}
