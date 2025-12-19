package com.example.healthcare.data.model

// Matches Class Diagram: LabTest
data class LabTest(
    val testId: String = "",       // Matches diagram: testId
    val testName: String = "",     // Matches diagram: testName
    val description: String = "",  // Matches diagram: description
    val price: Double = 0.0        // Matches diagram: price
)
