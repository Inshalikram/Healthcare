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

class LabTestDetailsActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_test_details)

        val tvPackageName: TextView = findViewById(R.id.tvPackageName)
        val tvTotalCost: TextView = findViewById(R.id.tvTotalCost)
        val edDetails: EditText = findViewById(R.id.edDetails)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Prevent editing details
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

            // Disable button to prevent double clicks
            btnAddToCart.isEnabled = false

            val price = try {
                cost!!.toFloat() 
            } catch (e: Exception) { 0f }
            
            val cartItem = CartItem(
                userId = userId,
                productName = name ?: "Unknown Lab Test",
                productPrice = price,
                productType = "LabTest"
            )

            // Use launch without immediately navigating away inside the scope which might be cancelled
            lifecycleScope.launch {
                repo.addToCart(cartItem).onSuccess {
                    if (!isDestroyed && !isFinishing) {
                        Toast.makeText(applicationContext, "Item Added to Cart Successfully", Toast.LENGTH_LONG).show()
                        // Use finish() to go back naturally instead of starting a new activity stack
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
