package com.bussiness.awpl.fragment.scheduledcallconsultation

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.ImageOnlyAdapter
import com.bussiness.awpl.databinding.FragmentScheduledCallConsulationBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.*
import com.bussiness.awpl.viewmodel.ScheduleCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@AndroidEntryPoint
class ScheduledCallConsultation : Fragment() {

    private var _binding: FragmentScheduledCallConsulationBinding? = null
    private val binding get() = _binding!!

    private var mediaUploadDialog: MediaUtils? = null
    private lateinit var imageOnlyAdapter: ImageOnlyAdapter
    private lateinit var viewModel: ScheduleCallViewModel

    private var age = ""
    private var diseaseId = 0
    private var gender = ""
    private var height = ""
    private var weight = ""
    private var name = ""

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            result.data?.data?.let { uri ->
//                mediaUploadDialog?.handleSelectedFile(uri)
//            }
//        }

        if (result.resultCode == Activity.RESULT_OK) {
            val selectedUris = mutableListOf<Uri>()

            // Check if multiple files are selected (ClipData)
            val clipData = result.data?.clipData
            if (clipData != null) {
                val count = clipData.itemCount
                for (i in 0 until count) {
                    val uri = clipData.getItemAt(i).uri
                    selectedUris.add(uri)
                }
            } else {
                // If only one file is selected (single Uri)
                result.data?.data?.let { uri ->
                    selectedUris.add(uri)
                }
            }

            // Now iterate through selectedUris to process the files
            selectedUris.forEach { uri ->
                if (currentType == "image" && MultipartUtil.isFileLargerThan2048KB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload an image that is less than 2 MB in size.")
                } else if (currentType == "video" && MultipartUtil.isFileLargerThan5MB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload a video that is less than 5 MB in size.")
                } else if (currentType == "PDF" && MultipartUtil.isFileLargerThan5MB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload a PDF that is less than 5 MB in size.")
                } else {
                    mediaUploadDialog?.handleSelectedFile(uri)
                }
            }
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduledCallConsulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ScheduleCallViewModel::class.java]
        setupRecyclerView()
        getArgumentsFromBundle()
        setupClickListeners()

        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        imageOnlyAdapter = ImageOnlyAdapter { mediaItem ->
            // Visibility check AFTER removing the image
            if (imageOnlyAdapter.getImageList().isEmpty()) {
                binding.viewImage.visibility = View.GONE
            }
        }


        binding.imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageOnlyAdapter
        }
    }

    private fun getArgumentsFromBundle() {
        arguments?.let { bundle ->
            age = bundle.getString(AppConstant.Age, "")
            height = bundle.getString(AppConstant.Height, "")
            weight = bundle.getString(AppConstant.Weight, "")
            diseaseId = bundle.getInt(AppConstant.DISEASE_ID, 0)
            name = bundle.getString(AppConstant.NAME, "")
            gender = bundle.getString(AppConstant.Gender, "")
        }
    }

    private fun setupClickListeners() {
        binding.btnImage.setOnClickListener {
            openMediaDialog("image")
        }

        binding.btnNext.setOnClickListener {
            if (validateInputs()) {
                submitForm()
            }
        }
    }

    private fun openMediaDialog(type: String) {
        if (type != "image") return // Only allow image for now

        mediaUploadDialog = MediaUtils(
            requireContext(),
            type,
            onFileSelected = { selectedFiles ->
                selectedFiles.forEach { uri ->
                    addMediaItem(uri, type)
                }
            },
            onBrowseClicked = { openImagePicker() }
        )
        mediaUploadDialog?.show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            setType("image/*")
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        imagePickerLauncher.launch(intent)
    }


    private fun addMediaItem(uri: Uri, type: String) {
        if (type.lowercase() != "image") return

        if (imageOnlyAdapter.getImageList().size >= 5) {
            LoadingUtils.showErrorDialog(requireContext(), "Only 5 images can be uploaded.")
            return
        }

        val mediaItem = MediaItem(MediaType.IMAGE, uri)
        imageOnlyAdapter.addImage(mediaItem)
        binding.viewImage.visibility = View.VISIBLE
    }

    private fun submitForm() {
        lifecycleScope.launch {
            val answer1 = MultipartUtil.stringToRequestBody(binding.ansNO1.text.toString())
            val answer2 = MultipartUtil.stringToRequestBody(binding.ansNo2.text.toString())
            val answer3 = MultipartUtil.stringToRequestBody(binding.ansNo3.text.toString())
            val answer4 = MultipartUtil.stringToRequestBody(binding.ans4.text.toString())
            val ageBody = MultipartUtil.stringToRequestBody(age)
            val heightBody = MultipartUtil.stringToRequestBody(height)
            val weightBody = MultipartUtil.stringToRequestBody(weight)
            val genderBody = MultipartUtil.stringToRequestBody(gender)
            val nameBody = MultipartUtil.stringToRequestBody(name)
            val diseaseBody = MultipartUtil.stringToRequestBody(diseaseId.toString())

            val multipartList = mutableListOf<MultipartBody.Part>()
            imageOnlyAdapter.getImageList().forEach { item ->
                MultipartUtil.uriToMultipart(requireContext(), item.uri)?.let { part ->
                    multipartList.add(part)
                }
            }

            LoadingUtils.showDialog(requireContext(), false)

            viewModel.scheduleCallForOther(
                answer1, answer2, answer3, answer4, diseaseBody,
                ArrayList(multipartList), nameBody, ageBody, heightBody, weightBody, genderBody
            ).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        LoadingUtils.showSuccessDialog(requireContext(), "Details Uploaded Successfully") {
                            val bundle = Bundle().apply {
                                putString(AppConstant.ID, result.data.toString())
                            }
                            findNavController().navigate(R.id.appointmentBooking, bundle)
                        }
                    }

                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(), result.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            if (ansNO1.text.isNullOrBlank()) {
                ansNO1.error = ErrorMessages.ERROR_MANDATORY
                ansNO1.requestFocus()
                return false
            }
            if (ansNo2.text.isNullOrBlank()) {
                ansNo2.error = ErrorMessages.ERROR_MANDATORY
                ansNo2.requestFocus()
                return false
            }
            if (ansNo3.text.isNullOrBlank()) {
                ansNo3.error = ErrorMessages.ERROR_MANDATORY
                ansNo3.requestFocus()
                return false
            }
            if (ans4.text.isNullOrBlank()) {
                ans4.error = ErrorMessages.ERROR_MANDATORY
                ans4.requestFocus()
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        binding.imageRecyclerView.adapter = null // Clean-up RecyclerView adapter
        _binding = null
        super.onDestroyView()
    }
}
