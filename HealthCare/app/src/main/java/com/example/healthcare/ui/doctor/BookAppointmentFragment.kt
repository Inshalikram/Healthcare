package com.example.healthcare.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthcare.data.model.Doctor
import com.example.healthcare.databinding.FragmentBookAppointmentBinding
import com.example.healthcare.viewmodel.AppointmentViewModel

class BookAppointmentFragment : Fragment() {

    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by viewModels()

    private var doctorName: String? = null
    private var doctorSpeciality: String? = null
    private var doctorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorName = it.getString("doctorName")
            doctorSpeciality = it.getString("doctorSpeciality")
            doctorId = it.getString("doctorId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind data
        binding.doctorName = doctorName ?: "Doctor Not Found"
        binding.doctorSpeciality = doctorSpeciality ?: "N/A"

        binding.btnBook.setOnClickListener {
            val date = binding.etDate.text.toString().trim()
            val time = binding.etTime.text.toString().trim()

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val dName = doctorName
            val dId = doctorId

            if (dName != null && dId != null) {
                // We reconstruct a Doctor object here, or fetch it if needed.
                // Since the ViewModel requires a Doctor object, we can create a temporary one
                // with the data we have, assuming these fields are enough for the appointment creation.
                // Ideally, we might pass the Doctor object itself via SafeArgs or shared ViewModel.
                val doctor = Doctor(dId, dName, doctorSpeciality ?: "")

                viewModel.bookAppointment(
                    doctor = doctor,
                    date = date,
                    time = time
                )
            } else {
                 Toast.makeText(requireContext(), "Doctor information missing", Toast.LENGTH_SHORT).show()
            }
        }

        observeResult()
    }

    private fun observeResult() {
        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Appointment booked successfully", Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }.onFailure {
                Toast.makeText(requireContext(), it.message ?: "Booking failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
