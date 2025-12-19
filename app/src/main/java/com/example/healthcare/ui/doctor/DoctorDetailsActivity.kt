package com.example.healthcare.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R
import com.example.healthcare.data.model.Doctor

class DoctorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        val tvName: TextView = findViewById(R.id.tvDoctorNameDD)
        val tvSpecialty: TextView = findViewById(R.id.tvSpecialtyDD)
        val tvHospital: TextView = findViewById(R.id.tvHospitalDD)
        val tvExperience: TextView = findViewById(R.id.tvExperienceDD)
        val tvMobile: TextView = findViewById(R.id.tvMobileDD)
        val tvFees: TextView = findViewById(R.id.tvFeesDD)
        val btnBook: Button = findViewById(R.id.btnBookAppointment)
        val btnBack: Button = findViewById(R.id.btnBackDD)

        // Retrieve Doctor object from Intent
        val doctor = intent.getSerializableExtra("doctor") as? Doctor

        if (doctor != null) {
            tvName.text = doctor.name
            tvSpecialty.text = doctor.specialty
            tvHospital.text = doctor.hospital
            tvExperience.text = "Exp: ${doctor.experience}"
            tvMobile.text = "Mobile: ${doctor.mobile}"
            tvFees.text = "Fees: ${doctor.fees}/-"

            btnBook.setOnClickListener {
                val intent = Intent(this, BookAppointmentActivity::class.java)
                intent.putExtra("doctor", doctor)
                startActivity(intent)
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
