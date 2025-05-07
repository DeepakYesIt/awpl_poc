package com.bussiness.awpl.fragment.symptomuploadScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.DialogReportDownloadBinding
import com.bussiness.awpl.databinding.FragmentSymptomUploadBinding
import com.bussiness.awpl.databinding.UploadSucessDialogBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.SymptomsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.ArrayList

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
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data

                uri?.let {
                    if(currentType =="image"&& MultipartUtil.isFileLargerThan2048KB(requireContext(),uri)){
                       LoadingUtils.showErrorDialog(requireContext(),"Please upload an image that is less than 2048 KB in size.")
                    }
                    else if(currentType =="video" && MultipartUtil.isFileLargerThan10MB(requireContext(),uri)){
                        LoadingUtils.showErrorDialog(requireContext(),"Please upload an video that is less than 10240 KB in size.")
                    }
                    else if(currentType == "PDF" && MultipartUtil.isFileLargerThan5MB(requireContext(),uri)){
                        LoadingUtils.showErrorDialog(requireContext(),"Please upload an pdf that is less than 5120 KB in size.")
                    }
                    else {
                        mediaUploadDialog?.handleSelectedFile(it)
                    }
                }
            }
        }
    private var diseaseId :Int = 0
    private var currentType: String = "" // Store type of media selected
    private lateinit var symptomsViewModel: SymptomsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSymptomUploadBinding.inflate(inflater, container, false)

        symptomsViewModel = ViewModelProvider(this)[SymptomsViewModel::class.java]

        arguments?.let {
            if(it.containsKey(AppConstant.DISEASE_ID)){
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
        imageAdapter = MediaAdapter(imageList) { mediaItem ->
            imageList.remove(mediaItem)
            imageAdapter.notifyDataSetChanged()
            binding.viewImage.visibility = if (imageList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        videoAdapter = MediaAdapter(videoList) { mediaItem ->
            videoList.remove(mediaItem)
            videoAdapter.notifyDataSetChanged()
            binding.viewVideo.visibility = if (videoList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        pdfAdapter = MediaAdapter(pdfList) { mediaItem ->
            pdfList.remove(mediaItem)
            pdfAdapter.notifyDataSetChanged()
            binding.ViewPdf.visibility = if (pdfList.isNotEmpty()) View.VISIBLE else View.GONE
        }

        binding.imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        binding.videoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = videoAdapter
        }

        binding.pdfRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = pdfAdapter
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnImage.setOnClickListener { openMediaDialog("image") }
            btnVideos.setOnClickListener { openMediaDialog("video") }
            btnPDF.setOnClickListener { openMediaDialog("PDF") }
            btnNext.setOnClickListener {
                if(validations()){
                    callingSymptomsApi()
                }
            }
        }
    }

    private fun callingSymptomsApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            var imageListMultiPart = mutableListOf<MultipartBody.Part>()
            var videoListMultiPart = mutableListOf<MultipartBody.Part>()
            var pdfListMultiPart = mutableListOf<MultipartBody.Part>()

            imageList.forEach {
                MultipartUtil.uriToMultipart(requireContext(),it.uri,"uploadImage[]")
                    ?.let { it1 -> imageListMultiPart.add(it1) }
            }

            videoList.forEach {
                MultipartUtil.uriToMultipart(requireContext(),it.uri,"uploadVideo[]")
                    ?.let { it1 -> videoListMultiPart.add(it1) }
            }

            pdfList.forEach {
                MultipartUtil.uriToMultipart(requireContext(),it.uri,"uploadPdf[]")
                    ?.let { it1 -> pdfListMultiPart.add(it1) }
            }

         var answer1 =   MultipartUtil.stringToRequestBody(binding.ansNO1.text.toString())
         var answer2 =   MultipartUtil.stringToRequestBody(binding.ansNo2.text.toString())
         var answer3 =   MultipartUtil.stringToRequestBody(binding.ansNo3.text.toString())
         var answer4 =   MultipartUtil.stringToRequestBody(binding.ans4.text.toString())
         var diseaseId = MultipartUtil.stringToRequestBody(Integer.toString(diseaseId))
            symptomsViewModel.symptomsUpload(answer1,answer2,answer3,answer4,
                ArrayList( imageListMultiPart),ArrayList(videoListMultiPart),
                ArrayList(pdfListMultiPart) ,diseaseId).collect{
                  when(it){
                      is NetworkResult.Success ->{
                          LoadingUtils.hideDialog()
                          successDialog()
                      }
                      is NetworkResult.Error ->{
                          LoadingUtils.hideDialog()
                          LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                      }
                      else ->{  }
                  }
           }
        }
    }


    private fun openMediaDialog(type: String) {
        currentType = type
        mediaUploadDialog = MediaUtils(requireContext(), type,
            onFileSelected = { selectedFiles -> selectedFiles.forEach { addMediaItem(it, type) } },
            onBrowseClicked = { openImagePicker(type) }
        )

        mediaUploadDialog?.show()
    }


    private fun openImagePicker(type: String) {
        val intent = when (type) {
            "video" -> Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            "PDF" -> {
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    setType("application/pdf")
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            else -> Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // Default image
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

                imageList.add(mediaItem)
                imageAdapter.notifyItemInserted(imageList.size - 1)
                binding.viewImage.visibility = View.VISIBLE
            }
            MediaType.VIDEO -> {
                videoList.add(mediaItem)
                videoAdapter.notifyItemInserted(videoList.size - 1)
                binding.viewVideo.visibility = View.VISIBLE
            }
            MediaType.PDF -> {
                pdfList.add(mediaItem)
                pdfAdapter.notifyItemInserted(pdfList.size - 1)
                binding.ViewPdf.visibility = View.VISIBLE
            }
        }
    }

    private fun validations() : Boolean {
        binding.apply {
            var isValid = true

            if(ansNO1.text.toString().isEmpty()){
                ansNO1.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }

            if(ansNo2.text.toString().isEmpty()){
                ansNo2.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }

            if(ansNo3.text.toString().isEmpty()){
                ansNo3.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }

            if(ans4.text.toString().isEmpty()){
                ans4.error = ErrorMessages.ERROR_MANDATORY
                isValid = false
            }

            return isValid
        }

    }

    private fun successDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = UploadSucessDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)

        // Apply width to the dialog window
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.btnOkay.setOnClickListener {
            dialog.dismiss()
            downloadReportDialog()
        }
        dialog.show()
    }

    private fun downloadReportDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogReportDownloadBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        // Set width with margin
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
        val dialogWidth = screenWidth - (2 * marginPx)

        // Apply width to the dialog window
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.btnOkay.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaUploadDialog = null
    }

}
