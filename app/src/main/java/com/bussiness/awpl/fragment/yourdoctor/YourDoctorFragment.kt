package com.bussiness.awpl.fragment.yourdoctor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.SummaryAdapter
import com.bussiness.awpl.adapter.YourDoctorAdapter
import com.bussiness.awpl.databinding.FragmentYourDoctorBinding
import com.bussiness.awpl.model.SummaryModel
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.DoctorViewModel
import com.bussiness.awpl.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YourDoctorFragment : Fragment() {

    private var _binding: FragmentYourDoctorBinding? = null
    private val binding get() = _binding!!

    private lateinit var  adapterDoctor :YourDoctorAdapter
    private val viewModel: DoctorViewModel by lazy {
        ViewModelProvider(this)[DoctorViewModel::class.java]
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        })

        setupRecyclerView()
        callingDoctorApi()
    }

    private fun setupRecyclerView() {

        adapterDoctor = YourDoctorAdapter(mutableListOf())

        binding.doctorRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterDoctor
        }
    }

    private fun callingDoctorApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.doctorList().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        Log.d("TESTING_DOCTOR",it.data?.size.toString())
                        it.data?.let {
                            Log.d("TESTING_DOCTOR",it.size.toString())
                          adapterDoctor.updateAdapter(it)

                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else->{

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
