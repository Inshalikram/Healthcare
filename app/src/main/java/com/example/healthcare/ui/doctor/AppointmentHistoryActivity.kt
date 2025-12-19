package com.example.healthcare.ui.doctor

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class AppointmentHistoryActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_history)

        val listView: ListView = findViewById(R.id.listViewAH)
        val tvNoData: TextView = findViewById(R.id.tvNoAppointments)

        val userId = repo.currentUserId()
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            repo.getAppointments(userId).onSuccess { appointments ->
                if (appointments.isEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    listView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    listView.visibility = View.VISIBLE

                    val list = ArrayList<HashMap<String, String>>()
                    for (app in appointments) {
                        val map = HashMap<String, String>()
                        map["line1"] = app.doctorName
                        map["line2"] = "Date: ${app.date} | Time: ${app.time}\nStatus: ${app.status}"
                        list.add(map)
                    }

                    val sa = SimpleAdapter(
                        this@AppointmentHistoryActivity,
                        list,
                        R.layout.multi_lines,
                        arrayOf("line1", "line2"),
                        intArrayOf(R.id.line_a, R.id.line_b)
                    )
                    listView.adapter = sa
                }
            }.onFailure {
                Toast.makeText(this@AppointmentHistoryActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
