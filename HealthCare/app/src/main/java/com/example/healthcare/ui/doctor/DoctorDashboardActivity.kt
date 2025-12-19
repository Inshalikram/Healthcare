package com.example.healthcare.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.healthcare.data.model.Appointment
import com.example.healthcare.data.repository.FirebaseRepository
import com.example.healthcare.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DoctorDashboardActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()
    private lateinit var listView: ListView
    private var appointments: List<Appointment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        listView = findViewById(R.id.listViewDD)
        val btnLogout: Button = findViewById(R.id.btnLogoutDD)

        loadAppointments()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val app = appointments[position]
            showActionDialog(app)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadAppointments() {
        // For demo, we might fetch all or filter by doctor ID if we had one set in profile
        // Here we assume the logged in user is a doctor and we fetch appointments for them
        // But since we don't have a doctor ID setup flow yet, let's just fetch ALL appointments for demo purposes
        // or fetch based on currentUserId if we assume doctorId == userId.
        
        val userId = repo.currentUserId() ?: return

        lifecycleScope.launch {
            // Ideally call repo.getDoctorAppointments(userId)
            // But since getDoctorAppointments was just added and might return empty if no appointments have matching doctorId
            // Let's modify getDoctorAppointments to query by doctorId field
            
            repo.getDoctorAppointments(userId).onSuccess { list ->
                // Filter only pending for dashboard action? Or show all
                appointments = list
                
                if (list.isEmpty()) {
                    findViewById<TextView>(R.id.tvNoAppointmentsDD).visibility = View.VISIBLE
                } else {
                    findViewById<TextView>(R.id.tvNoAppointmentsDD).visibility = View.GONE
                }

                val dataList = ArrayList<HashMap<String, String>>()
                for (app in list) {
                    val map = HashMap<String, String>()
                    map["line1"] = "Patient: ${app.userName.ifEmpty { "Unknown" }}"
                    map["line2"] = "Date: ${app.date} | Time: ${app.time}\nStatus: ${app.status}"
                    dataList.add(map)
                }

                val sa = SimpleAdapter(
                    this@DoctorDashboardActivity,
                    dataList,
                    R.layout.multi_lines,
                    arrayOf("line1", "line2"),
                    intArrayOf(R.id.line_a, R.id.line_b)
                )
                listView.adapter = sa
            }.onFailure {
                Toast.makeText(this@DoctorDashboardActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showActionDialog(app: Appointment) {
        val options = arrayOf("Approve", "Reject")
        AlertDialog.Builder(this)
            .setTitle("Manage Appointment")
            .setItems(options) { _, which ->
                val status = if (which == 0) "Approved" else "Rejected"
                updateStatus(app, status)
            }
            .show()
    }

    private fun updateStatus(app: Appointment, status: String) {
        lifecycleScope.launch {
            repo.updateAppointmentStatus(app.appointmentId, status).onSuccess {
                Toast.makeText(this@DoctorDashboardActivity, "Appointment $status", Toast.LENGTH_SHORT).show()
                loadAppointments()
            }.onFailure {
                Toast.makeText(this@DoctorDashboardActivity, "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
