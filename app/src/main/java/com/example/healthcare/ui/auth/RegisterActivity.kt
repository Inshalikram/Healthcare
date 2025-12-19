package com.example.healthcare.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcare.R
import com.example.healthcare.data.repository.FirebaseRepository
import com.example.healthcare.ui.doctor.DoctorDashboardActivity
import com.example.healthcare.ui.main.HomeActivity

class RegisterActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)
        val radioGroupRole = findViewById<RadioGroup>(R.id.radioGroupRole)
        val radioDoctor = findViewById<RadioButton>(R.id.radioDoctor)
        
        // Doctor details fields
        val layoutDoctorDetails = findViewById<LinearLayout>(R.id.layoutDoctorDetails)
        val spinnerSpecialty = findViewById<Spinner>(R.id.spinnerSpecialty)
        val etHospital = findViewById<EditText>(R.id.etHospital)
        val etExperience = findViewById<EditText>(R.id.etExperience)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etFees = findViewById<EditText>(R.id.etFees)

        // Setup Spinner
        val specialties = arrayOf("Family Physician", "Dietician", "Dentist", "Surgeon", "Cardiologists")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, specialties)
        spinnerSpecialty.adapter = adapter

        // Toggle visibility based on role
        radioGroupRole.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioDoctor) {
                layoutDoctorDetails.visibility = View.VISIBLE
            } else {
                layoutDoctorDetails.visibility = View.GONE
            }
        }

        btnRegister.setOnClickListener { 
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            val selectedRoleId = radioGroupRole.checkedRadioButtonId
            if (selectedRoleId == -1) {
                Toast.makeText(this, "Please select a role (Patient or Doctor)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val isDoctor = radioDoctor.isChecked
            val role = if(isDoctor) "doctor" else "patient"

            if (name.isEmpty() || email.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Enter valid name, email, and password (>=6 chars)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Validate doctor fields if role is doctor
            if (isDoctor) {
                if (etHospital.text.isEmpty() || etExperience.text.isEmpty() || etMobile.text.isEmpty() || etFees.text.isEmpty()) {
                    Toast.makeText(this, "Please fill all doctor details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            btnRegister.isEnabled = false

            repo.register(email, password) { success, error ->
                if (success) {
                    val user = repo.getCurrentUser()
                    if (user != null) {
                        // Gather extra details if doctor
                        val doctorDetails = if (isDoctor) {
                            mapOf(
                                "specialty" to spinnerSpecialty.selectedItem.toString(),
                                "hospital" to etHospital.text.toString(),
                                "experience" to etExperience.text.toString(),
                                "mobile" to etMobile.text.toString(),
                                "fees" to etFees.text.toString()
                            )
                        } else emptyMap()

                        repo.saveUserToFirestore(user.uid, name, email, role, doctorDetails) { saved, saveError ->
                            btnRegister.isEnabled = true
                            if (saved) {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()

                                // Redirect DIRECTLY to the correct dashboard
                                if (role == "doctor") {
                                    startActivity(Intent(this, DoctorDashboardActivity::class.java))
                                } else {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                }
                                finishAffinity() 

                            } else {
                                Toast.makeText(this, "Firestore error: $saveError", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        btnRegister.isEnabled = true
                        Toast.makeText(this, "User created but UID not found", Toast.LENGTH_LONG).show()
                    }
                } else {
                    btnRegister.isEnabled = true
                    Log.e("RegisterActivity", "Registration failed: $error")
                    Toast.makeText(this, "Registration failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
