package com.bussiness.awpl.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.activities.OnBoardActivity
import com.bussiness.awpl.adapter.ImageOnlyAdapter
import com.bussiness.awpl.adapter.MediaAdapter
import com.bussiness.awpl.databinding.FragmentHomeScheduleCallBinding
import com.bussiness.awpl.model.MediaItem
import com.bussiness.awpl.model.MediaType
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.utils.MediaUtils
import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.viewmodel.ScheduleCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@AndroidEntryPoint
class HomeScheduleCallFragment : Fragment() {

    private var _binding: FragmentHomeScheduleCallBinding? = null
    private val binding get() = _binding!!
    private var mediaUploadDialog: MediaUtils? = null
    private var currentType: String = ""
    private val mediaList = mutableListOf<MediaItem>()
//    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var imageOnlyAdapter: ImageOnlyAdapter
    var currentScreen :String = "for_me"
    private lateinit var viewModel :ScheduleCallViewModel
    private var diseaseId :Int =0
    private var type :String =""

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
                if ( MultipartUtil.isFileLargerThan2048KB(requireContext(), uri)) {
                    LoadingUtils.showErrorDialog(requireContext(), "Please upload an image that is less than 2 MB in size.")
                }  else {
                    mediaUploadDialog?.handleSelectedFile(uri)
                }
            }
   }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScheduleCallBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ScheduleCallViewModel::class.java]
        clickListener()
        setupRecyclerView()
        return binding.root
    }



    private fun selectTypeTask(type:String){
        if(type ==AppConstant.OTHERS) {
            // Button UI Updates
            Log.d("INSIDE_FRAG",type+" type here ")
            currentScreen="for_others"
            binding.apply {
                forOthers.setBackgroundResource(R.drawable.forother_btn)
                forOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forMe.setBackgroundResource(R.drawable.for_mr_bg)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                val bundle = Bundle().apply {
                    putString("TYPE", "forHome")
                    putInt(AppConstant.DISEASE_ID, diseaseId)
                    putBoolean(AppConstant.BACK,true)
                }
                this@HomeScheduleCallFragment.type =""
                findNavController().navigate(R.id.basicInfoScreen2, bundle)
                return
            }
        }
        else {
            // Button UI Updates
            currentScreen = "for_me"
            binding.apply {
                forMe.setBackgroundResource(R.drawable.forother_btn)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forOthers.setBackgroundResource(R.drawable.for_mr_bg)
                forOthers.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkGreyColor
                    )
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if(it.containsKey(AppConstant.DISEASE_ID)) {
                diseaseId = it.getInt(AppConstant.DISEASE_ID)
                type = it.getString(AppConstant.TYPE).toString()
                if(!type.isEmpty()){
                    selectTypeTask(type)
                }
            }
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

    private fun clickListener() {
        binding.apply {
            forOthers.setOnClickListener {
                // Button UI Updates
                currentScreen="for_others"
                forOthers.setBackgroundResource(R.drawable.forother_btn)
                forOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forMe.setBackgroundResource(R.drawable.for_mr_bg)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
                val bundle = Bundle().apply {
                    putString("TYPE", "forHome")
                    putInt(AppConstant.DISEASE_ID,diseaseId)
                }
                findNavController().navigate(R.id.basicInfoScreen2,bundle)
            }
            forMe.setOnClickListener {
                // Button UI Updates
                currentScreen="for_me"

                forMe.setBackgroundResource(R.drawable.forother_btn)
                forMe.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                forOthers.setBackgroundResource(R.drawable.for_mr_bg)
                forOthers.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreyColor))
            }
            btnNext.setOnClickListener {
                if (validations())
                    if(currentScreen.equals("for_me")){
                        callingScheduleCallForMe()
                    }else{
                        callingScheduleCallForOthers()
                    }
            }
            btnImage.setOnClickListener {
                openMediaDialog("image")
            }
        }
    }

    private fun callingScheduleCallForMe(){
        lifecycleScope.launch {
            var answer1 = MultipartUtil.stringToRequestBody(binding.ansNO1.text.toString())
            var answer2 = MultipartUtil.stringToRequestBody(binding.ansNo2.text.toString())
            var answer3 = MultipartUtil.stringToRequestBody(binding.ansNo3.text.toString())
            var answer4 = MultipartUtil.stringToRequestBody(binding.ans4.text.toString())
            var disease = MultipartUtil.stringToRequestBody(diseaseId.toString())
            var imgList = mutableListOf<MultipartBody.Part>()
            mediaList.forEach {
                MultipartUtil.uriToMultipart(requireContext(),it.uri,"uploadImage[]")
                    ?.let { it1 -> imgList.add(it1) }
            }
            Log.d("CHECKING_DISEASE_ID","Disease Id is working here is "+diseaseId.toString())

            LoadingUtils.showDialog(requireContext(),false)
            viewModel.scheduleCallForMe(answer1,answer2,answer3,answer4,disease,
                ArrayList(imgList)).collect(){

                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showSuccessDialog(requireContext(),"Data submitted successfully. You can now book an appointment."){
                            var bundle =Bundle()
                            bundle.putString(AppConstant.ID,it.data)
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

    private fun callingScheduleCallForOthers(){

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

    @SuppressLint("DefaultLocale")
    private fun addMediaItem(uri: Uri, type: String) {
        if (type.lowercase() != "image") return

        if (imageOnlyAdapter.getImageList().size >= 5) {
            LoadingUtils.showErrorDialog(requireContext(), "Only 5 images can be uploaded.")
            return
        }

        // Check if image already exists in mediaList
        if (mediaList.any { it.uri == uri }) {
            Toast.makeText(requireContext(), "This image is already uploaded", Toast.LENGTH_SHORT).show()
            return
        }

        val mediaItem = MediaItem(MediaType.IMAGE, uri)
        imageOnlyAdapter.addImage(mediaItem)
        mediaList.add(mediaItem)
        binding.viewImage.visibility = View.VISIBLE
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
