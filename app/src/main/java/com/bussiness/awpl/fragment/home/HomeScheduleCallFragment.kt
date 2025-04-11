package com.bussiness.awpl.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.activities.OnBoardActivity
import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentHomeScheduleCallBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.MediaUtils

class HomeScheduleCallFragment : Fragment() {

    private var _binding: FragmentHomeScheduleCallBinding? = null
    private val binding get() = _binding!!
    private var mediaUploadDialog: MediaUtils? = null
    private var currentType: String = ""
    private val mediaList = mutableListOf<MediaItem>()
    private lateinit var mediaAdapter: MediaAdapter

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    addMediaItem(it, currentType) // Pass the selected file URI
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScheduleCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        mediaAdapter = MediaAdapter(mediaList) { mediaItem ->
            mediaList.remove(mediaItem)
            if (mediaList.isEmpty()) {
                binding.viewImage.visibility = View.GONE
            }
        }
        binding.imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = mediaAdapter
        }
    }

    private fun clickListener() {
        binding.apply {
            forOthers.setOnClickListener {
                // Button UI Updates
                forOthers.setBackgroundResource(R.drawable.forother_btn)
                forOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forMe.setBackgroundResource(R.drawable.for_mr_bg)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                val bundle = Bundle().apply {
                    putString("TYPE", "forHome")
                }
                findNavController().navigate(R.id.basicInfoScreen2,bundle)
            }
            forMe.setOnClickListener {
                // Button UI Updates
                forMe.setBackgroundResource(R.drawable.forother_btn)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forOthers.setBackgroundResource(R.drawable.for_mr_bg)
                forOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
            }
            btnNext.setOnClickListener {
                if (validations())
                findNavController().navigate(R.id.appointmentBooking)
            }
            btnImage.setOnClickListener {
                openMediaDialog("image")
            }
        }
    }

    private fun openMediaDialog(type: String) {
        currentType = type
        mediaUploadDialog = MediaUtils(
            requireContext(),
            type,
            onFileSelected = { selectedFiles ->
                selectedFiles.forEach { addMediaItem(it, type) }
            },
            onBrowseClicked = {
                openImagePicker(type)
            }
        )
        mediaUploadDialog?.show()
    }

    private fun openImagePicker(type: String) {
        val intent = when (type) {
            "image" -> Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            else -> {
                Toast.makeText(requireContext(), "Unknown media type: $type", Toast.LENGTH_SHORT).show()
                return
            }
        }
        imagePickerLauncher.launch(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMediaItem(uri: Uri, type: String) {
        val mediaType = when (type.lowercase()) {
            "image" -> MediaType.IMAGE
            else -> throw IllegalArgumentException("Unknown media type: $type")
        }

        val mediaItem = MediaItem(mediaType, uri.toString())  // Convert Uri to String
        mediaList.add(mediaItem)
        mediaAdapter.notifyItemInserted(mediaList.size - 1)

        binding.viewImage.visibility = if (mediaList.any { it.type == MediaType.IMAGE }) View.VISIBLE else View.GONE
    }

    private fun validations(): Boolean {
        binding.apply {
            var isValid = true

            if (ansNO1.text.toString().isEmpty()) {
                ansNO1.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ansNo2.text.toString().isEmpty()) {
                ansNo2.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ansNo3.text.toString().isEmpty()) {
                ansNo3.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ans4.text.toString().isEmpty()) {
                ans4.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            return isValid
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
