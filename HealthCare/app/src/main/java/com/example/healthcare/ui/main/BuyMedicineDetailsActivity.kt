package com.example.healthcare.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.model.CartItem
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class BuyMedicineDetailsActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_medicine_details)

        val tvPackageName: TextView = findViewById(R.id.tvPackageNameBM)
        val tvTotalCost: TextView = findViewById(R.id.tvTotalCostBM)
        val edDetails: EditText = findViewById(R.id.edDetailsBM)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCartBM)
        val btnBack: Button = findViewById(R.id.btnBackBM)

        edDetails.keyListener = null

        val intent = intent
        val name = intent.getStringExtra("text1")
        val cost = intent.getStringExtra("text2")
        val details = intent.getStringExtra("text3")

        tvPackageName.text = name
        tvTotalCost.text = "Total Cost : $cost/-"
        edDetails.setText(details)

        btnBack.setOnClickListener {
            finish()
        }

        btnAddToCart.setOnClickListener {
            val userId = repo.currentUserId()
            if (userId == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button
            btnAddToCart.isEnabled = false

            val price = try {
                 cost!!.toFloat() 
            } catch (e: Exception) { 0f }
            
            val cartItem = CartItem(
                userId = userId,
                productName = name ?: "Unknown Medicine",
                productPrice = price,
                productType = "Medicine"
            )

            lifecycleScope.launch {
                repo.addToCart(cartItem).onSuccess {
                    if (!isDestroyed && !isFinishing) {
                         Toast.makeText(applicationContext, "Item Added to Cart Successfully", Toast.LENGTH_LONG).show()
                         finish()
                    }
                }.onFailure {
                    if (!isDestroyed && !isFinishing) {
                         btnAddToCart.isEnabled = true
                         Toast.makeText(applicationContext, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
