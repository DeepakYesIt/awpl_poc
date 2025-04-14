package com.bussiness.awpl.fragment.home

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.BrowseVideoAdapter
import com.bussiness.awpl.adapter.HealthJourneyAdapter
import com.bussiness.awpl.adapter.OrganListAdapter
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.FragmentHomeBinding
import com.bussiness.awpl.model.HealthJourneyItem


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var organListAdapter: OrganListAdapter
    private lateinit var healthJourneyAdapter: HealthJourneyAdapter
    private lateinit var browseVideoAdapter: BrowseVideoAdapter
    private val viewModel: HomeViewModel by viewModels()
    private val healthJourneyList = listOf(
        HealthJourneyItem("Begin Your Health\n Journey with a \nFree Consultation!", R.drawable.women_doctor),
        HealthJourneyItem("Bringing Doctors\n to Your Door – \nVirtually.", R.drawable.women_doctor),
        HealthJourneyItem("Upload Symptoms \nfor Minor Issues \nand Major Concerns", R.drawable.women_doctor)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        clickListener()
    }

    private fun setupRecyclerViews() {
        // Organ List RecyclerView
        binding.deptRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            organListAdapter = OrganListAdapter(viewModel.organList)
            adapter = organListAdapter
        }

        // Health Journey RecyclerView
        binding.bannerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            healthJourneyAdapter = HealthJourneyAdapter(healthJourneyList) { item ->
                findNavController().navigate(R.id.homeScheduleCallFragment)
            }
            adapter = healthJourneyAdapter
        }

        // Browse Video RecyclerView
        binding.videoRecyclerView.apply {
            browseVideoAdapter = BrowseVideoAdapter(viewModel.browseVideoList) { item ->
                Toast.makeText(requireContext(), "Playing Video: ${item.title}", Toast.LENGTH_SHORT).show()
            }
            adapter = browseVideoAdapter
        }
    }

    private fun clickListener() {
        binding.apply {
            txtSeeAllDisease.setOnClickListener { findNavController().navigate(R.id.diseasesBottomFragment) }
            symptomUploadBtn.setOnClickListener { findNavController().navigate(R.id.symptomUpload) }
            seeAllVideos.setOnClickListener     { findNavController().navigate(R.id.videoGalleryFragment) }
            scheduleCallBtn.setOnClickListener  { findNavController().navigate(R.id.homeScheduleCallFragment) }
            upcomingSeeAll.setOnClickListener   { findNavController().navigate(R.id.scheduleFragment) }
            rescheduleButton.setOnClickListener { findNavController().navigate(R.id.appointmentBooking) }
            cancelBtn?.setOnClickListener       { cancelDialog() }
        }
    }

    private fun cancelDialog() {
        val dialog = Dialog(requireContext())
        val binding = DialogCancelAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNo.setOnClickListener { dialog.dismiss() }
            btnYes.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
