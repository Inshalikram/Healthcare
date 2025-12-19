package com.example.healthcare.data.model

// Matches Class Diagram: Appointment
data class Appointment(
    var appointmentId: String = "", // Matches diagram: appointmentId (int -> String for Firebase)
    var date: String = "",          // Matches diagram: date
    var time: String = "",          // Matches diagram: time
    var status: String = "pending", // Matches diagram: status
    var patientId: String = "",     // Matches diagram: patientId (FK)
    var doctorId: String = "",      // Matches diagram: doctorId (FK)
    
    // Extra fields for UI convenience (not strictly in diagram but needed for display)
    var doctorName: String = "",
    var userName: String = ""
)
