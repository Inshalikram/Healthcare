package com.example.healthcare.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.healthcare.R
import com.example.healthcare.ui.auth.LoginActivity
import com.example.healthcare.ui.doctor.AppointmentHistoryActivity
import com.example.healthcare.ui.main.BuyMedicineActivity
import com.example.healthcare.util.NotificationService
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    // Register the permission callback
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startNotificationService()
        } else {
            Toast.makeText(this, "Notification permission denied. You will not receive appointment updates.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // Ask for permission and start the service
        askForNotificationPermission()

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

    private fun askForNotificationPermission() {
        // This is only required for API level 33+ (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted, start the service
                    startNotificationService()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Optional: show a dialog explaining why you need the permission
                    // For now, we will just request it directly.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older Android versions, permission is granted by default
            startNotificationService()
        }
    }

    private fun startNotificationService() {
        startService(Intent(this, NotificationService::class.java))
    }
}
