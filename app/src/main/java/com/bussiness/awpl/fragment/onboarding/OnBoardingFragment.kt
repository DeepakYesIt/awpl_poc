package com.bussiness.awpl.fragment.onboarding

import com.bussiness.awpl.adapter.OnboardingAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentOnBoardingBinding
import com.bussiness.awpl.model.OnboardingItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnGetStarted: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onboardingItems = listOf(
            OnboardingItem(R.drawable.onboard_1_ic, getString(R.string.onboarding_1)),
            OnboardingItem(R.drawable.onboard_2_ic, getString(R.string.onboarding_2)),
            OnboardingItem(R.drawable.onboard_3_ic, getString(R.string.onboarding_3))
        )

        viewPager = binding.viewPager
        tabIndicator = binding.tabIndicator
        btnGetStarted = binding.btnGetStarted

        viewPager.adapter = OnboardingAdapter(onboardingItems)

        TabLayoutMediator(tabIndicator, viewPager) { tab, _ ->
            tab.setCustomView(R.layout.custom_tab)
        }.attach()

        updateTabDots(0) // Initialize first active dot

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == onboardingItems.lastIndex) {
                    btnGetStarted.visibility = View.VISIBLE
                } else {
                    btnGetStarted.visibility = View.GONE
                }
                updateTabDots(position)
            }
        })

        btnGetStarted.setOnClickListener {
            // Navigation to onboarding screen
            findNavController().navigate(R.id.welcomeScreen1)
        }
    }

    private fun updateTabDots(selectedPosition: Int) {
        for (i in 0 until tabIndicator.tabCount) {
            val tab = tabIndicator.getTabAt(i)
            val imageView = tab?.customView?.findViewById<ImageView>(R.id.tabDot)
            if (i == selectedPosition) {
                imageView?.setImageResource(R.drawable.big_dot)
            } else {
                imageView?.setImageResource(R.drawable.small_dot)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}