package com.example.healthcare.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.healthcare.R
import com.example.healthcare.ui.auth.LoginActivity
import com.example.healthcare.ui.doctor.AppointmentHistoryActivity
import com.example.healthcare.ui.main.BuyMedicineActivity
import com.example.healthcare.util.NotificationService
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // Start Notification Service to listen for appointment updates
        startService(Intent(this, NotificationService::class.java))

        val cardLabTest: CardView = findViewById(R.id.cardLabTest)
        val cardBuyMedicine: CardView = findViewById(R.id.cardBuyMedicine)
        val cardFindDoctor: CardView = findViewById(R.id.cardFindDoctor)
        val cardHealthArticles: CardView = findViewById(R.id.cardHealthArticles)
        val cardOrderDetails: CardView = findViewById(R.id.cardOrderDetails)
        val cardLogout: CardView = findViewById(R.id.cardLogout)

        cardLabTest.setOnClickListener {
            startActivity(Intent(this, LabTestActivity::class.java))
        }

        cardBuyMedicine.setOnClickListener {
            startActivity(Intent(this, BuyMedicineActivity::class.java))
        }

        cardFindDoctor.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        cardHealthArticles.setOnClickListener {
             startActivity(Intent(this, HealthArticlesActivity::class.java))
        }

        cardOrderDetails.setOnClickListener {
            startActivity(Intent(this, OrderDetailsActivity::class.java))
        }

        cardLogout.setOnClickListener {
            // Stop service on logout
            stopService(Intent(this, NotificationService::class.java))
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
