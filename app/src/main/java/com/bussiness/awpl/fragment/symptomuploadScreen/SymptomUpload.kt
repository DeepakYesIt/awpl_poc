package com.bussiness.awpl.fragment.symptomuploadScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentSymptomUploadBinding
import com.bussiness.awpl.databinding.UploadSucessDialogBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.SymptomsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SymptomUpload : Fragment() {

    private var _binding: FragmentSymptomUploadBinding? = null
    private val binding get() = _binding!!
    private var mediaUploadDialog: MediaUtils? = null
    private val imageList = mutableListOf<MediaItem>()
    private val videoList = mutableListOf<MediaItem>()
    private val pdfList = mutableListOf<MediaItem>()
    private lateinit var imageAdapter: MediaAdapter
    private lateinit var videoAdapter: MediaAdapter
    private lateinit var pdfAdapter: MediaAdapter
    private var diseaseId: Int = 0
    private var currentType: String = ""
    private lateinit var symptomsViewModel: SymptomsViewModel

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedUris = mutableListOf<Uri>()
                val clipData = result.data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        selectedUris.add(clipData.getItemAt(i).uri)
                    }
                }
                else {
                    result.data?.data?.let { selectedUris.add(it) }
                }

                selectedUris.forEach { uri ->
                    mediaUploadDialog?.handleSelectedFile(uri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSymptomUploadBinding.inflate(inflater, container, false)
        symptomsViewModel = ViewModelProvider(this)[SymptomsViewModel::class.java]

        arguments?.let {
            if (it.containsKey(AppConstant.DISEASE_ID)) {
                diseaseId = it.getInt(AppConstant.DISEASE_ID)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        imageAdapter = MediaAdapter(imageList) {
            imageList.remove(it)
            imageAdapter.notifyDataSetChanged()
            binding.viewImage.visibility = if (imageList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        videoAdapter = MediaAdapter(videoList) {
            videoList.remove(it)
            videoAdapter.notifyDataSetChanged()
            binding.viewVideo.visibility = if (videoList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        pdfAdapter = MediaAdapter(pdfList) {
            pdfList.remove(it)
            pdfAdapter.notifyDataSetChanged()
            binding.ViewPdf.visibility = if (pdfList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        binding.imageRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = imageAdapter

        binding.videoRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.videoRecyclerView.adapter = videoAdapter

        binding.pdfRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pdfRecyclerView.adapter = pdfAdapter
    }

    private fun setupClickListeners() {
        binding.apply {
            btnImage.setOnClickListener { openMediaDialog("image") }
            btnVideos.setOnClickListener { openMediaDialog("video") }
            btnPDF.setOnClickListener { openMediaDialog("pdf") }
            btnNext.setOnClickListener {
                if (validations()) {
                    callingSymptomsApi()
                }
            }
        }
    }

    private fun openMediaDialog(type: String) {
        val listSize = when (type.lowercase()) {
            "image" -> imageList.size
            "video" -> videoList.size
            "pdf" -> pdfList.size
            else -> 0
        }

        if (listSize >= MAX_ITEMS) {
            LoadingUtils.showErrorDialog(requireContext(), "You’ve already uploaded $MAX_ITEMS $type files.")
            return
        }

        if (mediaUploadDialog?.isShowing == true) return

        currentType = type
        mediaUploadDialog = MediaUtils(requireContext(), type,
            onFileSelected = { selectedFiles -> selectedFiles.forEach { addMediaItem(it, type) } },
            onBrowseClicked = { openImagePicker(type) }
        )
        mediaUploadDialog?.show()
    }

    private fun openImagePicker(type: String) {
        val intent = when (type.lowercase()) {
            "video" -> Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            "pdf" -> Intent(Intent.ACTION_GET_CONTENT).apply {
                setType("application/pdf")
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            else -> Intent(Intent.ACTION_GET_CONTENT).apply {
                setType("image/*")
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
        imagePickerLauncher.launch(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMediaItem(uri: Uri, type: String) {
        val mediaType = when (type.lowercase()) {
            "image" -> MediaType.IMAGE
            "video" -> MediaType.VIDEO
            "pdf" -> MediaType.PDF
            else -> throw IllegalArgumentException("Unknown media type: $type")
        }

        val mediaItem = MediaItem(mediaType, uri)

        when (mediaType) {
            MediaType.IMAGE -> {
                Log.d("TESTING_IMAGE","Inside Image List")
                if (imageList.size >= MAX_ITEMS) {
                    LoadingUtils.showErrorDialog(requireContext(), "Only $MAX_ITEMS images can be uploaded.")
                    return
                }
                if (imageList.any { it.uri == uri }) {
                    Log.d("TESTING_IMAGE","Image List has already contain it")
                    showAlreadyUploadedToast("image")
                    return
                }
                if (MultipartUtil.isFileLargerThan2048KB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload an image less than 2 MB.")
                    return
                }
                imageList.add(mediaItem)
                imageAdapter.notifyItemInserted(imageList.size - 1)
                binding.viewImage.visibility = View.VISIBLE
            }

            MediaType.VIDEO -> {
                if (videoList.size >= MAX_ITEMS) {
                    LoadingUtils.showErrorDialog(requireContext(), "Only $MAX_ITEMS videos can be uploaded.")
                    return
                }
                if (videoList.any { it.uri == uri }) {
                    showAlreadyUploadedToast("video")
                    return
                }
                if (MultipartUtil.isFileLargerThan5MB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload a video less than 5 MB.")
                    return
                }
                videoList.add(mediaItem)
                videoAdapter.notifyItemInserted(videoList.size - 1)
                binding.viewVideo.visibility = View.VISIBLE
            }

            MediaType.PDF -> {
                if (pdfList.size >= MAX_ITEMS) {
                    LoadingUtils.showErrorDialog(requireContext(), "Only $MAX_ITEMS PDFs can be uploaded.")
                    return
                }
                if (pdfList.any { it.uri == uri }) {
                    showAlreadyUploadedToast("PDF")
                    return
                }
                if (MultipartUtil.isFileLargerThan5MB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload a PDF less than 5 MB.")
                    return
                }
                pdfList.add(mediaItem)
                pdfAdapter.notifyItemInserted(pdfList.size - 1)
                binding.ViewPdf.visibility = View.VISIBLE
            }
        }
    }

    private fun showAlreadyUploadedToast(type: String) {
        Toast.makeText(requireContext(), "This $type is already uploaded", Toast.LENGTH_SHORT).show()
    }

    private fun validations(): Boolean {
        binding.apply {
            var isValid = true
            if (ansNO1.text.isNullOrEmpty()) {
                ansNO1.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ansNo2.text.isNullOrEmpty()) {
                ansNo2.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ansNo3.text.isNullOrEmpty()) {
                ansNo3.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            if (ans4.text.isNullOrEmpty()) {
                ans4.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }
            return isValid
        }
    }

    private fun callingSymptomsApi() {
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(), false)

            val imageParts = imageList.mapNotNull { MultipartUtil.uriToMultipart(requireContext(), it.uri, "uploadImage[]") }
            val videoParts = videoList.mapNotNull { MultipartUtil.uriToMultipart(requireContext(), it.uri, "uploadVideo[]") }
            val pdfParts = pdfList.mapNotNull { MultipartUtil.uriToMultipart(requireContext(), it.uri, "uploadPdf[]") }

            val answer1 = MultipartUtil.stringToRequestBody(binding.ansNO1.text.toString())
            val answer2 = MultipartUtil.stringToRequestBody(binding.ansNo2.text.toString())
            val answer3 = MultipartUtil.stringToRequestBody(binding.ansNo3.text.toString())
            val answer4 = MultipartUtil.stringToRequestBody(binding.ans4.text.toString())
            val diseaseIdPart = MultipartUtil.stringToRequestBody(diseaseId.toString())

            symptomsViewModel.symptomsUpload(
                answer1, answer2, answer3, answer4,
                ArrayList(imageParts), ArrayList(videoParts),
                ArrayList(pdfParts), diseaseIdPart
            ).collect {
                when (it) {
                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        successDialog()
                    }

                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(), it.message.toString())
                    }

                    else -> {}
                }
            }
        }
    }

    private fun successDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = UploadSucessDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        dialog.window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)

        dialogBinding.btnOkay.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaUploadDialog = null
    }

    companion object {
        private const val MAX_ITEMS = 5
    }
}



