package com.example.healthcare.data.model

import java.io.Serializable

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val hospital: String = "HealthCare Hospital",
    val experience: String = "5 Years",
    val mobile: String = "1234567890",
    val fees: String = "500"
) : Serializable
