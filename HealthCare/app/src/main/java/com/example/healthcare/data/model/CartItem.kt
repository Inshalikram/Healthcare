package com.example.healthcare.data.model

data class CartItem(
    val id: String = "",
    val userId: String = "",
    val productName: String = "",
    val productPrice: Float = 0f,
    val productType: String = "" // "LabTest" or "Medicine"
)
