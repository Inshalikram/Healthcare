package com.example.healthcare.ui.doctor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcare.R
import com.example.healthcare.data.model.Appointment
import com.example.healthcare.data.model.Doctor
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class BookAppointmentActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_book_appointment) 

        val tvTitle: TextView = findViewById(R.id.tvTitleBookAppointment)
        val tvSubTitle: TextView = findViewById(R.id.tvSubTitleBookAppointment)
        val etDate: EditText = findViewById(R.id.etDate)
        val etTime: EditText = findViewById(R.id.etTime)
        val btnBook: Button = findViewById(R.id.btnBook)
       
        val doctor = intent.getSerializableExtra("doctor") as? Doctor

        if (doctor != null) {
            tvTitle.text = doctor.name
            tvSubTitle.text = doctor.specialty
        }
        
        // Prevent manual typing to enforce using the picker
        etDate.keyListener = null
        etTime.keyListener = null
        
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
                    // Convert to AM/PM format
                    val amPm = if (hourOfDay >= 12) "PM" else "AM"
                    val hourIn12Format = if (hourOfDay > 12) hourOfDay - 12 else if (hourOfDay == 0) 12 else hourOfDay
                    val minuteStr = if (minute < 10) "0$minute" else minute.toString()
                    etTime.setText("$hourIn12Format:$minuteStr $amPm")
                },
                hour,
                minute,
                false // false for AM/PM format, true for 24h
            )
            timePickerDialog.show()
        }

        btnBook.setOnClickListener {
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = repo.currentUserId() ?: return@setOnClickListener

            val appointment = Appointment(
                patientId = userId, 
                userName = "", 
                doctorId = doctor?.id ?: "",
                doctorName = doctor?.name ?: "",
                date = date,
                time = time,
                status = "pending"
            )

            lifecycleScope.launch {
                repo.createAppointment(appointment).onSuccess {
                    Toast.makeText(this@BookAppointmentActivity, "Appointment booked successfully", Toast.LENGTH_LONG).show()
                    finish()
                }.onFailure {
                    Toast.makeText(this@BookAppointmentActivity, "Booking failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
