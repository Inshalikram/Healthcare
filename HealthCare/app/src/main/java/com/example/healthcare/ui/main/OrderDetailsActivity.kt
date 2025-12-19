package com.example.healthcare.ui.main

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.model.CartItem
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailsActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()
    private lateinit var listView: ListView
    private var cartItems: List<CartItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        listView = findViewById(R.id.listViewOD)
        
        // Initial load
        loadCartItems()

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            if (position >= 0 && position < cartItems.size) {
                val selectedItem = cartItems[position]
                if (selectedItem.id.isNotEmpty()) {
                    showDeleteDialog(selectedItem)
                }
            }
            true 
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure data is fresh when returning to this screen
        loadCartItems()
    }

    private fun loadCartItems() {
        val userId = repo.currentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            repo.getCartItems(userId).onSuccess { items ->
                if (isDestroyed || isFinishing) return@onSuccess

                cartItems = items
                val list = ArrayList<HashMap<String, String>>()
                
                for (item in items) {
                    val map = HashMap<String, String>()
                    map["line1"] = item.productName
                    map["line2"] = "Price: " + item.productPrice + "/- (" + item.productType + ")"
                    list.add(map)
                }

                val sa = SimpleAdapter(
                    this@OrderDetailsActivity,
                    list,
                    R.layout.multi_lines,
                    arrayOf("line1", "line2"),
                    intArrayOf(R.id.line_a, R.id.line_b)
                )
                listView.adapter = sa

            }.onFailure {
                if (!isDestroyed && !isFinishing) {
                    // Don't show toast for cancellation, or maybe log it
                    // Toast.makeText(this@OrderDetailsActivity, "Failed to load cart: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteDialog(item: CartItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to remove ${item.productName} from your cart?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteItem(item: CartItem) {
        // Optimistic update: Remove from list locally first to show immediate effect
        val currentList = ArrayList(cartItems)
        currentList.remove(item)
        cartItems = currentList
        
        // Update adapter immediately
        val listForAdapter = ArrayList<HashMap<String, String>>()
        for (itm in cartItems) {
            val map = HashMap<String, String>()
            map["line1"] = itm.productName
            map["line2"] = "Price: " + itm.productPrice + "/- (" + itm.productType + ")"
            listForAdapter.add(map)
        }
        val sa = SimpleAdapter(
            this@OrderDetailsActivity,
            listForAdapter,
            R.layout.multi_lines,
            arrayOf("line1", "line2"),
            intArrayOf(R.id.line_a, R.id.line_b)
        )
        listView.adapter = sa

        // Perform actual delete in background
        lifecycleScope.launch {
            repo.deleteCartItem(item.id).onSuccess {
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Item removed", Toast.LENGTH_SHORT).show()
                    // Reload to sync fully
                    loadCartItems()
                }
            }.onFailure {
                // If it fails, reload to bring the item back
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    loadCartItems()
                }
            }
        }
    }
}
