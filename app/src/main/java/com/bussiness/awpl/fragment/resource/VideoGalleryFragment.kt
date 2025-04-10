package com.bussiness.awpl.fragment.resource

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.VideoGalleryAdapter
import com.bussiness.awpl.databinding.FragmentVideoGalleryBinding
import com.bussiness.awpl.model.HealthJourneyItem

class VideoGalleryFragment : Fragment() {

    private var _binding: FragmentVideoGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoGalleryAdapter: VideoGalleryAdapter
    private lateinit var mainActivity: HomeActivity


    private val videoList = listOf(
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
        HealthJourneyItem("Lorem ipsum dolor sit amet, adipiscing elit, dolor sit amet", R.drawable.lady_dc),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as HomeActivity
//        mainActivity.setUpToolBarIconText("video_gallery")

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.videoGalleryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            videoGalleryAdapter = VideoGalleryAdapter(videoList) { item ->
                Toast.makeText(requireContext(), "Playing Video: ${item.title}", Toast.LENGTH_SHORT).show()
            }
            adapter = videoGalleryAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Bottom Navigation again when leaving FAQFragment
        (requireActivity() as? HomeActivity)?.findViewById<View>(R.id.homeBottomNav)?.visibility = View.VISIBLE
        _binding = null
    }
}
