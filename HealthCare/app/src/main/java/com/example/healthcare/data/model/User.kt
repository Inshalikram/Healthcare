package com.example.healthcare.data.model

// Matches Class Diagram: User
open class User(
    val userId: String = "",       // Matches diagram: userId
    val name: String = "",         // Matches diagram: name
    val email: String = "",        // Matches diagram: email
    val role: String = "patient"   // Matches diagram: role
) {
    // Methods from diagram (Conceptual placeholders)
    fun login(): Boolean = true
    fun logout() {}
    fun updateProfile() {}
}
