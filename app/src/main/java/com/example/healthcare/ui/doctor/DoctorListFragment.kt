package com.example.healthcare.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcare.R
import com.example.healthcare.data.model.Doctor
import com.example.healthcare.databinding.FragmentDoctorListBinding
import com.example.healthcare.viewmodel.DoctorViewModel

class DoctorListFragment : Fragment() {

    private var _binding: FragmentDoctorListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorViewModel by viewModels()
    private lateinit var doctorAdapter: DoctorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.loadDoctors()
    }

    private fun setupRecyclerView() {
        // Changed lambda to accept Doctor object and open Details Activity
        doctorAdapter = DoctorAdapter { doctor ->
            val intent = Intent(requireContext(), DoctorDetailsActivity::class.java)
            intent.putExtra("doctor", doctor)
            startActivity(intent)
        }

        binding.doctorRecyclerView.apply {
            adapter = doctorAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.doctors.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE

            result.onSuccess { list ->
                if (list.isEmpty()) {
                    Toast.makeText(requireContext(), "No doctors found", Toast.LENGTH_SHORT).show()
                } else {
                    doctorAdapter.submitList(list)
                }
            }.onFailure { error ->
                Toast.makeText(requireContext(), error.message ?: "Failed to load doctors", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
