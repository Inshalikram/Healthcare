package com.example.healthcare.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.model.CartItem
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class LabTestDetailsActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_test_details)

        val tvPackageName: TextView = findViewById(R.id.tvPackageName)
        val tvTotalCost: TextView = findViewById(R.id.tvTotalCost)
        val edDetails: EditText = findViewById(R.id.edDetails)
        val etDate: EditText = findViewById(R.id.etDateLTD)
        val etTime: EditText = findViewById(R.id.etTimeLTD)
        val cbHomeCollection: CheckBox = findViewById(R.id.cbHomeCollection)
        val etAddress: EditText = findViewById(R.id.etAddress)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)
        val btnBack: Button = findViewById(R.id.btnBack)

        // Prevent editing where pickers are used
        edDetails.keyListener = null
        etDate.keyListener = null
        etTime.keyListener = null

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
        
        // Show/Hide Address field based on CheckBox
        cbHomeCollection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etAddress.visibility = View.VISIBLE
            } else {
                etAddress.visibility = View.GONE
            }
        }
        
        // Date Picker
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    etDate.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        
        // Time Picker
        etTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val amPm = if (hourOfDay >= 12) "PM" else "AM"
                    val hourIn12Format = if (hourOfDay > 12) hourOfDay - 12 else if (hourOfDay == 0) 12 else hourOfDay
                    val minuteStr = if (minute < 10) "0$minute" else minute.toString()
                    etTime.setText("$hourIn12Format:$minuteStr $amPm")
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }

        btnAddToCart.setOnClickListener {
            val userId = repo.currentUserId()
            if (userId == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Validate date and time
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please select date and time for sample collection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Validate address if home collection selected
            var finalAddress = ""
            if (cbHomeCollection.isChecked) {
                val addr = etAddress.text.toString().trim()
                if (addr.isEmpty()) {
                    Toast.makeText(this, "Please enter address for home collection", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                finalAddress = " [Home Visit: $addr]"
            }

            btnAddToCart.isEnabled = false

            val price = try {
                cost!!.toFloat() 
            } catch (e: Exception) { 0f }
            
            // Append scheduling info to product name so it appears in order details
            val scheduledName = "$name ($date $time)$finalAddress"
            
            val cartItem = CartItem(
                userId = userId,
                productName = scheduledName,
                productPrice = price,
                productType = "LabTest"
            )

            lifecycleScope.launch {
                repo.addToCart(cartItem).onSuccess {
                    if (!isDestroyed && !isFinishing) {
                        Toast.makeText(applicationContext, "Lab Test Scheduled & Added to Cart", Toast.LENGTH_LONG).show()
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
