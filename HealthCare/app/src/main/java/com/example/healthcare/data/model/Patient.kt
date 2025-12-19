package com.example.healthcare.data.model

// Matches Class Diagram: Patient (extends User)
class Patient(
    userId: String,
    name: String,
    email: String,
    val medicalHistory: String = "", // Matches diagram: medicalHistory
    val phone: String = ""           // Matches diagram: phone
) : User(userId, name, email, "patient") {
    
    // Methods from diagram
    fun bookAppointment() {}
    fun viewAppointments() {}
    fun viewLabResults() {}
}
