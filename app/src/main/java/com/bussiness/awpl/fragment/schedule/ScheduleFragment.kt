package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.activities.OnBoardActivity
import com.bussiness.awpl.databinding.FragmentScheduleBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private var selectedTab = 0
    private lateinit var mainActivity: HomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as HomeActivity
//        mainActivity.setUpToolBarIconText("my_appointments")
        showTemporaryData()
        clickListener()
        selectTab(selectedTab)
    }

    private fun selectTab(index: Int) {
        selectedTab = index

        binding.apply {
            // Reset all to grey and hide underline
            txtUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))
            txtCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))
            txtCanceled.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))

            viewUpcoming.visibility = View.INVISIBLE
            viewCompleted.visibility = View.INVISIBLE
            viewCanceled.visibility = View.INVISIBLE

            // Highlight selected tab
            when (index) {
                0 -> {
                    txtUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewUpcoming.visibility = View.VISIBLE
                    loadFragment(UpcomingFragment())
                }
                1 -> {
                    txtCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCompleted.visibility = View.VISIBLE
                    loadFragment(CompletedFragment())
                }
                2 -> {
                    txtCanceled.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                    viewCanceled.visibility = View.VISIBLE
                    loadFragment(CancelledFragment())
                }
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            txtUpcoming.setOnClickListener { selectTab(0) }
            txtCompleted.setOnClickListener { selectTab(1) }
            txtCanceled.setOnClickListener { selectTab(2) }
//            btnNext.setOnClickListener { val intent = Intent(requireContext(), OnBoardActivity::class.java).apply { putExtra("LOAD_FRAGMENT", "schedule") }
//                startActivity(intent)}
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.appointmentBooking)
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.innerFragmentContainer, fragment)
            .commit()
    }

    private fun showTemporaryData() {
        binding.apply {
            noDataView.visibility = View.VISIBLE
            innerFragmentContainer.visibility = View.GONE

            lifecycleScope.launch {
                delay(3000)
                noDataView.visibility = View.GONE
                innerFragmentContainer.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
