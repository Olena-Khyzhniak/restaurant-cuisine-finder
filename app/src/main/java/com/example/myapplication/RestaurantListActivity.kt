package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.models.RestaurantModel
import com.google.android.material.navigation.NavigationView

// Displays the full list of restaurants using a RecyclerView.
// Features:
//  • Toolbar with a hamburger icon that opens the Navigation Drawer
//  • Up / Back arrow provided by parentActivityName in the manifest
//  • SearchView at the top for live filtering by cuisine
//  • Navigation Drawer with quick-links to Add and About
class RestaurantListActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var drawerLayout: DrawerLayout

    // Full restaurant list — used to restore after a search clears
    private var allRestaurants: List<RestaurantModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)

        // ── Toolbar ──────────────────────────────────────────────────────────
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Show Up/Back arrow — parent is MainActivity (declared in the manifest)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ── Navigation Drawer ────────────────────────────────────────────────
        drawerLayout = findViewById(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.menu_view_restaurants,   // open description (accessibility)
            R.string.menu_add_restaurant      // close description (accessibility)
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // ── RecyclerView ─────────────────────────────────────────────────────
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestaurantAdapter(
            restaurants = emptyList(),
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

        // ── SearchView with Cuisine Dropdown List ────────────────────────────
        val searchView = findViewById<SearchView>(R.id.searchView)
        val searchAutoComplete = searchView.findViewById<android.widget.AutoCompleteTextView>(
            androidx.appcompat.R.id.search_src_text
        )
        val cuisines = resources.getStringArray(R.array.cuisine_types)
        val searchAdapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            cuisines
        )
        searchAutoComplete.setAdapter(searchAdapter)
        searchAutoComplete.threshold = 1

        // When a cuisine is selected from the drop-down list, populate search field
        searchAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedCuisine = parent.getItemAtPosition(position) as String
            searchView.setQuery(selectedCuisine, true)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            // Filter the list as the user types — matches title OR cuisine
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrBlank()) {
                    allRestaurants
                } else {
                    val q = newText.trim().lowercase()
                    allRestaurants.filter { r ->
                        r.cuisine.lowercase().contains(q) ||
                        r.title.lowercase().contains(q)
                    }
                }
                adapter.updateData(filtered)
                return true
            }
        })
    }

    // Called every time the screen becomes visible — keeps the list up to date
    // after returning from AddEditActivity
    override fun onResume() {
        super.onResume()
        refreshList()
    }

    // Fetches the latest data from AppData and updates the RecyclerView
    private fun refreshList() {
        allRestaurants = AppData.restaurants.findAll()
        adapter.updateData(allRestaurants)
    }

    // ── Navigation Drawer item clicks ─────────────────────────────────────────
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            R.id.nav_view_restaurants -> { /* already here */ }
            R.id.nav_add_restaurant   -> startActivity(Intent(this, AddEditActivity::class.java))
            R.id.nav_about            -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Close drawer on Back press if it is open; otherwise normal back
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Up arrow — navigate back to MainActivity (as declared in the manifest)
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return super.onSupportNavigateUp()
    }
}
