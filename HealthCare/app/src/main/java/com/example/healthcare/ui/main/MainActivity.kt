package com.example.healthcare.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.healthcare.R
import com.example.healthcare.ui.doctor.DoctorListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_doctor)

        val exit: CardView = findViewById(R.id.cardFDBack)
        exit.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val familyPhysician: CardView = findViewById(R.id.cardFDFamilyPhysician)
        familyPhysician.setOnClickListener {
            val it = Intent(this, DoctorListActivity::class.java)
            it.putExtra("title", "Family Physician")
            startActivity(it)
        }

        val dietician: CardView = findViewById(R.id.cardFDDietician)
        dietician.setOnClickListener {
            val it = Intent(this, DoctorListActivity::class.java)
            it.putExtra("title", "Dietician")
            startActivity(it)
        }

        val dentist: CardView = findViewById(R.id.cardFDDentist)
        dentist.setOnClickListener {
            val it = Intent(this, DoctorListActivity::class.java)
            it.putExtra("title", "Dentist")
            startActivity(it)
        }

        val surgeon: CardView = findViewById(R.id.cardFDSurgeon)
        surgeon.setOnClickListener {
            val it = Intent(this, DoctorListActivity::class.java)
            it.putExtra("title", "Surgeon")
            startActivity(it)
        }

        val cardiologists: CardView = findViewById(R.id.cardFDCardiologists)
        cardiologists.setOnClickListener {
            val it = Intent(this, DoctorListActivity::class.java)
            it.putExtra("title", "Cardiologists")
            startActivity(it)
        }
    }
}
