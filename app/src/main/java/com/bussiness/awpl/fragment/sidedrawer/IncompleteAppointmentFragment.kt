package com.bussiness.awpl.fragment.sidedrawer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.IncompleteAppointAdapter
import com.bussiness.awpl.databinding.FragmentIncompleteAppointmentBinding
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.MyAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IncompleteAppointmentFragment : Fragment() {

    private var _binding: FragmentIncompleteAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var incompleteAdapter: IncompleteAppointAdapter
    private lateinit var viewModel: MyAppointmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncompleteAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MyAppointmentViewModel::class.java]
        setUpRecyclerView()
        incompleteAppointmentApiCall()
    }

    private fun setUpRecyclerView() {
        incompleteAdapter = IncompleteAppointAdapter(mutableListOf()) { appointment ->
            val bundle = Bundle().apply {
                putInt(AppConstant.AppoitmentId, appointment.id)
            }
            findNavController().navigate(R.id.reschedule_call, bundle)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incompleteAdapter
        }
    }


    private fun incompleteAppointmentApiCall() {
        viewLifecycleOwner.lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(), false)
            viewModel.incompleteAppointment().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        val appointmentList = result.data ?: emptyList()
                        Log.d("TESTING_WORK", "Size is ${appointmentList.size}")

                        if (appointmentList.isNotEmpty()) {
                            binding.noDataView.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                        } else {
                            binding.noDataView.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }

                        incompleteAdapter.updateAdapter(appointmentList)
                    }

                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(), result.message ?: "Unknown error")
                    }

                    else -> {
                        // Handle Loading or Idle state if needed
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

