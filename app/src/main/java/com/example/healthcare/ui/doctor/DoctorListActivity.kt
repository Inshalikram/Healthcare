package com.example.healthcare.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcare.R
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class DoctorListActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        recyclerView = findViewById(R.id.doctorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val title = intent.getStringExtra("title") ?: "Doctors"
        supportActionBar?.title = title

        lifecycleScope.launch {
            repo.getDoctors().onSuccess { doctors ->
                val filteredList = doctors.filter { d -> d.specialty.equals(title, ignoreCase = true) }
                
                val listToShow = if (filteredList.isNotEmpty()) filteredList else doctors
                
                if (filteredList.isEmpty() && doctors.isNotEmpty()) {
                     Toast.makeText(this@DoctorListActivity, "No doctors found for $title. Showing all.", Toast.LENGTH_SHORT).show()
                }

                val adapter = DoctorAdapter { doctor ->
                    val intent = Intent(this@DoctorListActivity, DoctorDetailsActivity::class.java)
                    intent.putExtra("doctor", doctor)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
                adapter.submitList(listToShow)
                
            }.onFailure {
                Toast.makeText(this@DoctorListActivity, "Failed to load doctors: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
