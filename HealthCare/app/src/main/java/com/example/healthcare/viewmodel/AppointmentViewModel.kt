package com.example.healthcare.viewmodel

// --- REQUIRED IMPORTS ---
// These are needed for ViewModel, LiveData, coroutines, and your data models.
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcare.data.model.Appointment
import com.example.healthcare.data.model.Doctor
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
// --- CLASS DEFINITION ---
// Your class must extend ViewModel to get access to viewModelScope.
class AppointmentViewModel : ViewModel() {

    // --- (1) REPOSITORY INSTANCE ---
    // The ViewModel holds a private reference to the repository to get data.
    private val repository = FirebaseRepository()

    // --- (2) LIVEDATA FOR UI ---
    // A private MutableLiveData that the ViewModel can change.
    private val _createResult = MutableLiveData<Result<Unit>>()
    // A public, unchangeable LiveData that the Fragment observes for results.
    val createResult: LiveData<Result<Unit>> = _createResult

    // --- (3) YOUR FUNCTION ---
    // This function is now correctly placed inside the class.
    fun bookAppointment(doctor: Doctor, date: String, time: String) {
        // Use viewModelScope to launch a background task safely. This scope is
        // automatically cancelled when the ViewModel is cleared, preventing memory leaks.
        viewModelScope.launch {
            val userId = repository.currentUserId()
            if (userId == null) {
                // Update LiveData with a failure result.
                _createResult.value = Result.failure(Exception("User not logged in"))
                return@launch
            }

            // Create the Appointment data object.
            val appointment = Appointment(
                userId = userId,
                userName = "", // TODO: Get from logged in user profile
                doctorId = doctor.id,
                doctorName = doctor.name,
                date = date,
                time = time
            )
            // Call the repository's suspend function and post the result.
            _createResult.value = repository.createAppointment(appointment)
        }
    }
}


