package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.model.CartItem
import com.example.healthcare.data.repository.FirebaseRepository
import com.example.healthcare.ui.doctor.BookAppointmentActivity
import kotlinx.coroutines.launch

class OrderDetailsActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()
    private lateinit var listView: ListView
    private lateinit var tvTotalCost: TextView
    private lateinit var btnCheckout: Button
    private var cartItems: List<CartItem> = ArrayList()
    private var totalAmount: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        listView = findViewById(R.id.listViewOD)
        tvTotalCost = findViewById(R.id.tvTotalCostOD)
        btnCheckout = findViewById(R.id.btnCheckoutOD)
        
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

        btnCheckout.setOnClickListener {
            val userId = repo.currentUserId()
            if (userId != null && cartItems.isNotEmpty()) {
                // Confirm Checkout
                AlertDialog.Builder(this)
                    .setTitle("Checkout")
                    .setMessage("Place order for Total: $totalAmount/- ?")
                    .setPositiveButton("Yes") { _, _ ->
                        placeOrder(userId)
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
                totalAmount = 0f
                
                for (item in items) {
                    val map = HashMap<String, String>()
                    map["line1"] = item.productName
                    map["line2"] = "Price: " + item.productPrice + "/- (" + item.productType + ")"
                    list.add(map)
                    totalAmount += item.productPrice
                }
                
                tvTotalCost.text = "Total Cost: $totalAmount/-"

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
                    // Toast.makeText(this@OrderDetailsActivity, "Failed to load cart: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun placeOrder(userId: String) {
        lifecycleScope.launch {
            repo.placeOrder(userId, cartItems, totalAmount).onSuccess {
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                    loadCartItems() // Refresh to show empty cart
                }
            }.onFailure {
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Failed to place order: ${it.message}", Toast.LENGTH_SHORT).show()
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
        val currentList = ArrayList(cartItems)
        currentList.remove(item)
        cartItems = currentList
        
        // Recalculate total immediately for UI responsiveness
        var tempTotal = 0f
        val listForAdapter = ArrayList<HashMap<String, String>>()
        for (itm in cartItems) {
            val map = HashMap<String, String>()
            map["line1"] = itm.productName
            map["line2"] = "Price: " + itm.productPrice + "/- (" + itm.productType + ")"
            listForAdapter.add(map)
            tempTotal += itm.productPrice
        }
        tvTotalCost.text = "Total Cost: $tempTotal/-"
        
        val sa = SimpleAdapter(
            this@OrderDetailsActivity,
            listForAdapter,
            R.layout.multi_lines,
            arrayOf("line1", "line2"),
            intArrayOf(R.id.line_a, R.id.line_b)
        )
        listView.adapter = sa

        lifecycleScope.launch {
            repo.deleteCartItem(item.id).onSuccess {
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Item removed", Toast.LENGTH_SHORT).show()
                    loadCartItems()
                }
            }.onFailure {
                if (!isDestroyed && !isFinishing) {
                    Toast.makeText(this@OrderDetailsActivity, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    loadCartItems()
                }
            }
        }
    }
}
