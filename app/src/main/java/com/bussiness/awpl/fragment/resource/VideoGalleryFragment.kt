package com.bussiness.awpl.fragment.resource

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.VideoGalleryAdapter
import com.bussiness.awpl.databinding.FragmentVideoGalleryBinding
import com.bussiness.awpl.model.HealthJourneyItem
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoGalleryFragment : Fragment() {

    private var _binding: FragmentVideoGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoGalleryAdapter: VideoGalleryAdapter

    private val videoViewModel: VideoViewModel by lazy {
        ViewModelProvider(this)[VideoViewModel::class.java]
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }


    private fun openYouTubeUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setPackage("com.google.android.youtube") // Try YouTube app
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If YouTube app not found, open in browser
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }


    fun isYouTubeUrl(url: String): Boolean {
        val youtubeRegex = Regex("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+$")
        return youtubeRegex.matches(url.trim())
    }



    private fun setupRecyclerView() {
        binding.videoGalleryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            videoGalleryAdapter = VideoGalleryAdapter(mutableListOf()) { item ->

                if(isYouTubeUrl(item.video_link)){
                    openYouTubeUrl(requireContext(),item.video_link)
                }
                else{
                    openVideo(item.video_link)
                }
            }
            adapter = videoGalleryAdapter
        }

        callingVideoApi()

    }
    private fun openVideo(videoUrl :String){

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(videoUrl), "video/*")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun callingVideoApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            videoViewModel.video().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()

                        it.data?.let { it1 ->
                            Log.d("TESTING_URL","HERE INSIDE THE VIDEO GALLERY SIZE "+ it1.size)
                            videoGalleryAdapter.updateAdapter(it1) }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else ->{

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
