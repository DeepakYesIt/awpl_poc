package com.bussiness.awpl.fragment.yourdoctor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.SummaryAdapter
import com.bussiness.awpl.adapter.YourDoctorAdapter
import com.bussiness.awpl.databinding.FragmentYourDoctorBinding
import com.bussiness.awpl.model.SummaryModel

class YourDoctorFragment : Fragment() {

    private var _binding: FragmentYourDoctorBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: HomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as HomeActivity
//        mainActivity.setUpToolBarIconText("your_doctors")

        setupRecyclerView()
    }



    private fun setupRecyclerView() {
        val doctorList = listOf(
            SummaryModel(R.drawable.doctor_image, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.women_doctor, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.doctor_icon2, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.portraite_doctor, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.doctor_image, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.women_doctor, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.doctor_icon2, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.portraite_doctor, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.doctor_image, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.women_doctor, "Doctor’s name", "Experience: 2 Years"),
            SummaryModel(R.drawable.doctor_image, "Doctor’s name", "Experience: 2 Years")
        )

        val adapter = YourDoctorAdapter(doctorList)
        binding.doctorRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
