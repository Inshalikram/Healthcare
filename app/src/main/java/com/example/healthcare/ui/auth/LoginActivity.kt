package com.example.healthcare.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.databinding.ActivityLoginBinding
import com.example.healthcare.data.repository.FirebaseRepository
import com.example.healthcare.ui.doctor.DoctorDashboardActivity
import com.example.healthcare.ui.main.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Already logged in? -> Redirect based on role
        repo.getCurrentUser()?.let {
            redirectUser(it.uid)
            return // Finish onCreate early
        }

        binding.btnLogin.setOnClickListener { loginUser() }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false

        repo.login(email, password) { success, error ->
            binding.btnLogin.isEnabled = true
            if (success) {
                val user = repo.getCurrentUser()
                if (user != null) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    redirectUser(user.uid)
                } else {
                    Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    this,
                    error ?: "Login failed. Check credentials.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun redirectUser(uid: String) {
        lifecycleScope.launch {
            repo.getUserRole(uid).onSuccess {
                if (it.equals("doctor", ignoreCase = true) || it.equals("admin", ignoreCase = true)) {
                    startActivity(Intent(this@LoginActivity, DoctorDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                }
                finish()
            }.onFailure {
                // Default to patient dashboard on error
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        }
    }
}
