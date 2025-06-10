package com.bussiness.awpl.fragment.scheduledcallconsultation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentScheduledCallConsulationBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.ScheduleCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.ArrayList

@AndroidEntryPoint
class ScheduledCallConsultation : Fragment() {

    private var _binding : FragmentScheduledCallConsulationBinding? = null

    private val binding get() = _binding!!

    private var mediaUploadDialog: MediaUtils? = null

    private var currentType: String = ""

    private val mediaList = mutableListOf<MediaItem>()

    private lateinit var mediaAdapter: MediaAdapter

    private var age :String    =""
    private var diseaseId :Int =0
    private var gender :String =""
    private var height :String = ""
    private var weight :String =""
    private var name :String =""

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    // Show in dialog and fragment
                    mediaUploadDialog?.handleSelectedFile(it)
                }
            }
    }
    private lateinit var viewModel :ScheduleCallViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduledCallConsulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ScheduleCallViewModel::class.java]
        setUpRecyclerView()
        arguments?.let {

            it.let { bundle ->
                age = bundle.getString(AppConstant.Age)?.takeIf { it.isNotBlank() } ?: ""
                height = bundle.getString(AppConstant.Height, "")
                weight = bundle.getString(AppConstant.Weight, "")
                diseaseId = bundle.getInt(AppConstant.DISEASE_ID, 0)  // Use -1 or a safe default
                name = bundle.getString(AppConstant.NAME, "")
                gender = bundle.getString(AppConstant.Gender, "")
            }

        }

        clickListeners()

        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

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

    private fun clickListeners() {
        binding.apply {
            btnImage.setOnClickListener { openMediaDialog("image") }
            btnNext.setOnClickListener {
                if (validations()){
                    callingScheduleForOtherApi()

                }
            }
        }
    }


    private fun callingScheduleForOtherApi(){
        lifecycleScope.launch {
            var answer1 = MultipartUtil.stringToRequestBody(binding.ansNO1.text.toString())
            var answer2 = MultipartUtil.stringToRequestBody(binding.ansNo2.text.toString())
            var answer3 = MultipartUtil.stringToRequestBody(binding.ansNo3.text.toString())
            var answer4 = MultipartUtil.stringToRequestBody(binding.ans4.text.toString())
            var ageRequest = MultipartUtil.stringToRequestBody(age.toString())
            var heightRequest = MultipartUtil.stringToRequestBody(height.toString())
            var weightRequest = MultipartUtil.stringToRequestBody(weight.toString())
            var gender = MultipartUtil.stringToRequestBody(gender.toString())
            var fullName  = MultipartUtil.stringToRequestBody(name.toString())
            var diseaseId = MultipartUtil.stringToRequestBody(diseaseId.toString())
            var imgList = mutableListOf<MultipartBody.Part>()
            mediaList.forEach {
                MultipartUtil.uriToMultipart(requireContext(),it.uri)
                    ?.let { it1 -> imgList.add(it1) }
            }
            LoadingUtils.showDialog(requireContext(),false)

            viewModel.scheduleCallForOther(
                answer1, answer2, answer3, answer4, diseaseId, ArrayList(imgList), fullName, ageRequest, heightRequest,
                weightRequest,
                gender
            ).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showSuccessDialog(requireContext(),"Details Uploaded Successfully"){
                            var bundle = Bundle().apply {
                                putString(AppConstant.ID,it.data.toString())
                            }
                            findNavController().navigate(R.id.appointmentBooking,bundle)
                        }
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

    private fun openMediaDialog(type: String) {
        currentType = type
        mediaUploadDialog = MediaUtils(
            requireContext(), type, onFileSelected = { selectedFiles ->
                selectedFiles.forEach {
                    addMediaItem(it, type)
                }
            },
            onBrowseClicked = { openImagePicker(type) }
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

        val mediaItem = MediaItem(mediaType, uri)  // Convert Uri to String
        mediaList.add(mediaItem)
        mediaAdapter.notifyItemInserted(mediaList.size - 1)

        binding.viewImage.visibility = if (mediaList.any { it.type == MediaType.IMAGE }) View.VISIBLE else View.GONE
    }

    private fun validations(): Boolean {
        binding.apply {
            val isValid = true

            if (ansNO1.text.toString().isEmpty()) {
                ansNO1.error = ErrorMessages.ERROR_MANDATORY
                ansNO1.requestFocus()
                return false
            }
            if (ansNo2.text.toString().isEmpty()) {
                ansNo2.error = ErrorMessages.ERROR_MANDATORY
                ansNo2.requestFocus()
                return  false
            }
            if (ansNo3.text.toString().isEmpty()) {
                ansNo3.error = ErrorMessages.ERROR_MANDATORY
                ansNo3.requestFocus()
                return false
            }
            if (ans4.text.toString().isEmpty()) {
                ans4.error = ErrorMessages.ERROR_MANDATORY
                ans4.requestFocus()
                return false
            }
            return isValid
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
