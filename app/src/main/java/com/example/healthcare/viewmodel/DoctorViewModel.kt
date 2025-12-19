package com.example.healthcare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcare.data.model.Doctor
import com.example.healthcare.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _doctors = MutableLiveData<Result<List<Doctor>>>()
    val doctors: LiveData<Result<List<Doctor>>> = _doctors

    /**
     * Fetches the list of doctors from the repository.
     */
    fun loadDoctors() {
        viewModelScope.launch {
            _doctors.value = repository.getDoctors()
        }
    }
}
