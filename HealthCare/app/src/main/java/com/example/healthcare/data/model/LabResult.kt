package com.example.healthcare.data.model

// Matches Class Diagram: LabResult
data class LabResult(
    val resultId: String = "",     // Matches diagram: resultId
    val patientId: String = "",    // Matches diagram: patientId
    val testId: String = "",       // Matches diagram: testId (implicit relation)
    val resultFile: String = "",   // Matches diagram: resultFile
    val date: String = ""          // Matches diagram: date
)
