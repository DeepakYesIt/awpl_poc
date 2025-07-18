package com.bussiness.awpl.fragment.sidedrawer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.net.toUri
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.StateAdapter
import com.bussiness.awpl.base.CommonUtils
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogFullImageBinding
import com.bussiness.awpl.databinding.DialogLogoutBinding
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.MyProfileViewModel
import com.bussiness.awpl.viewmodel.MyprofileModel
import com.bussiness.awpl.viewmodel.PrivacyViewModel

import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart
import java.net.URLConnection

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val PREFS_NAME = "ProfilePrefs"
    private val IMAGE_URI_KEY = "profile_image_uri"
    private var imageProfileMultiPart :MultipartBody.Part? = null
    private var selectedImageUri: Uri? = null
    private var originalProfileData: MyprofileModel? = null
    private var selectedState =""

    private val viewModel: MyProfileViewModel by lazy {
        ViewModelProvider(this)[MyProfileViewModel::class.java]
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                selectedImageUri = uri
                binding.profileImage.setImageURI(uri)
                imageProfileMultiPart =
                    selectedImageUri?.let {
                        uriToMultipart(requireContext(),
                            it,"profileImage")
                    }
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val imageUri = saveBitmapToInternalStorage(it) // Save bitmap & get URI
                selectedImageUri = imageUri
                Glide.with(requireContext()).load(imageUri).into(binding.profileImage)

                Log.d("TESTING_MULTIPART","URI IS:- "+imageUri)
                imageProfileMultiPart = uriToMultipart(requireContext(),imageUri,"profileImage")
                Log.d("TESTING_MULTIPART", imageProfileMultiPart.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        callingProfileApi()

        val recyclerView = binding.stateRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())




        binding.etState.setText(selectedState)
        recyclerView.adapter = StateAdapter(AppConstant.statesAndUTs) { it ->
            binding.etState.setText(it)
            selectedState= it
            binding.myCard.visibility =View.GONE
        }
        binding.etState.setOnClickListener {
            if(binding.myCard.isVisible){
                binding.myCard.visibility =View.GONE
            }else{
                binding.myCard.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSavedImage()
        val textViews = listOf(binding.txtMale, binding.txtFemale, binding.txtOthers)
        disableAllEdlitText()
        textViews.forEach { textView ->
            textView.setOnClickListener {
                if(binding.llEditDelete.visibility == View.GONE) {
                    updateSelection(textView, textViews)
                }
            }
        }
        binding.etHeight.setOnClickListener { showHeightPickerDialog() }
        clickListener()
    }

    private fun callingProfileApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.myProfile().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        it.data?.let { it1 -> settingDataToUi(it1) }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun settingDataToUi(data: MyprofileModel) {
        originalProfileData = data
        binding.apply {

            etFullName.text =SessionManager(requireContext()).getUserName()

                 data.name?.let {

                     etName.setText(data.name)
                 }
            data.ds_code?.let {
                textView39.text ="DsCode: ${it}"
            }
            data.state?.let {
                AppConstant.statesAndUTs.forEach {
                    if(it.equals(data.state,ignoreCase = true)){
                        selectedState = it
                        binding.etState.text = selectedState
                        return@forEach
                    }
                }
            }
                 data.email?.let {
                     etEmail.text = it
                 }
                 data.contact_no?.let {
                     etPhoneNumber.text =it
                 }
                 data.height?.let {
                     etHeight.text = it
                 }
                 data.weight?.let {
                     etweight.setText(it)
                 }
                 data.age.let {
                       etAge.setText(it.toString())
                 }
                 val textViews = listOf(binding.txtMale, binding.txtFemale, binding.txtOthers)
                 data.gender?.let {
                     when(it){
                         "male" ->{
                             updateSelection(binding.txtMale, textViews)
                         }
                         "female" ->{
                             updateSelection(binding.txtFemale, textViews)
                         }
                         "others"->{
                             updateSelection(binding.txtOthers, textViews)
                         }
                     }
                 }
                 Glide.with(requireContext()).load(AppConstant.Base_URL+ data.profile_path?.let {
                     ensureStartsWithSlash(
                         it
                     )
                 }).placeholder(R.drawable.ic_not_found_img).into(profileImage)
             }
       }

    private fun ensureStartsWithSlash(path: String): String {
        return if (path.startsWith("/")) path else "/$path"
    }

    private fun showHeightPickerDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_height_picker, null)
        val feetPicker = dialogView.findViewById<NumberPicker>(R.id.feetPicker)
        val inchPicker = dialogView.findViewById<NumberPicker>(R.id.inchPicker)

        // Configure pickers
        feetPicker.minValue = 3
        feetPicker.maxValue = 8

        inchPicker.minValue = 0
        inchPicker.maxValue = 11

        // Optionally: Try to pre-fill based on existing text
        val currentText = binding.etHeight.text.toString()
        val feetMatch = Regex("(\\d+)\\s*ft").find(currentText)?.groupValues?.get(1)?.toIntOrNull()
        val inchMatch = Regex("(\\d+)\\s*in").find(currentText)?.groupValues?.get(1)?.toIntOrNull()

        feetPicker.value = feetMatch ?: 5
        inchPicker.value = inchMatch ?: 6

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Height")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val selectedFeet = feetPicker.value
                val selectedInches = inchPicker.value
                binding.etHeight.setText("$selectedFeet ft $selectedInches in")
            }
            .setNegativeButton("Cancel", null)

        var dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#FFC107"))
        }

        dialog.show()
    }

    private fun updateSelection(selected: TextView, allTextViews: List<TextView>) {
        allTextViews.forEach { textView ->
            if (textView == selected) {
                textView.setTextColor("#FFFFFF".toColorInt())
                textView.setTypeface(null, Typeface.BOLD)
                textView.setBackgroundColor("#199FD9".toColorInt())
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyColor))
                textView.setTypeface(null, Typeface.NORMAL)
                textView.setBackgroundColor("#DAEFF9".toColorInt())
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            editIcon.setOnClickListener { openImageChooser() }
            btnDeleteAccount.setOnClickListener { dialogDeleteAccount() }
            binding.rlEditProfile.setOnClickListener {
                binding.llEditDelete.visibility = View.GONE
                binding.textView11.setText(R.string.enter_your_height)
                binding.textView12.setText(R.string.enter_your_weight)
                binding.llSaveCancel.visibility =View.VISIBLE
                enableAllEdlitText()
            }
            binding.rlSave.setOnClickListener {
                if(validateFields()) {
                    callingProfileSaveApi()
                }
            }
            binding.rlCancel.setOnClickListener {
                binding.llEditDelete.visibility = View.VISIBLE
                binding.llSaveCancel.visibility = View.GONE
                disableAllEdlitText()
                // Restore original data
                originalProfileData?.let { data ->
                    settingDataToUi(data)
                }
            }
            profileImage.setOnClickListener {
                showFullImageDialog()
            }
        }
    }

    private fun showFullImageDialog() {
        val dialog = Dialog(requireContext())
        val binding = DialogFullImageBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        // Load image
        val imageUrl = AppConstant.Base_URL + originalProfileData?.profile_path?.let { ensureStartsWithSlash(it) }
        if(selectedImageUri != null){
            binding.fullImageView.setImageURI(selectedImageUri)
        }else{
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_not_found_img)
                .into(binding.fullImageView)
        }


        // Dismiss dialog when image is tapped
        binding.fullImageView.setOnClickListener {
            dialog.dismiss()
        }

        // Apply dialog window styles
        dialog.apply {
            setCancelable(true) // Set only once
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    fun isValidName(name: String): Boolean {
        val namePattern = "^[A-Za-z]+( [A-Za-z]+)*$"
        return name.matches(Regex(namePattern))
    }

    private fun validateFields(): Boolean {
        binding.apply {
            var isValid = true
            if (etName.text.toString().isEmpty()) {
                etName.error = ErrorMessages.ERROR_NAME
                etName.requestFocus()
                return  false
            }
            if(!isValidName(etName.text.toString())){
                etName.error = "Please enter a valid name. It should only contain letters and spaces — no numbers or special characters"
                etName.requestFocus()
                return false
            }

            if (etHeight.text.toString().isEmpty()) {
                LoadingUtils.showErrorDialog(requireContext(),"Height selection is required")
                return false
            }
            if (etweight.text.toString().isEmpty()) {
                etweight.error = ErrorMessages.ERROR_WEIGHT
                etweight.requestFocus()
                return false
            }
            if (etAge.text.toString().isEmpty()) {
                etAge.error = ErrorMessages.ERROR_AGE
                etAge.requestFocus()
                return false
            }

            if(Integer.parseInt(etAge.text.toString()) <12 || Integer.parseInt(etAge.text.toString()) >120){
                etAge.error = "Age should be greater than 11 years and less than 120 years"
                etAge.requestFocus()
                return false
            }

            val selectedGender = listOf(txtMale, txtFemale, txtOthers).any {
                it.currentTextColor == "#FFFFFF".toColorInt()
            }

            if (!selectedGender) {
                txtOthers.error = ErrorMessages.ERROR_GENDER
                txtOthers.requestFocus()
                return false
            }
            return isValid
        }
    }

    private fun callingProfileSaveApi(){
        lifecycleScope.launch {
            val fullName = MultipartUtil.stringToRequestBody(binding.etName.text.toString())
            val height = MultipartUtil.stringToRequestBody(binding.etHeight.text.toString())
            val weight = MultipartUtil.stringToRequestBody(binding.etweight.text.toString())
            val age = MultipartUtil.stringToRequestBody(binding.etAge.text.toString())
            val state = MultipartUtil.stringToRequestBody(binding.etState.text.toString())
            val selectedGender = when {
                binding.txtMale.currentTextColor == Color.parseColor("#FFFFFF") -> "male"
                binding.txtFemale.currentTextColor == Color.parseColor("#FFFFFF") -> "female"
                binding.txtOthers.currentTextColor == Color.parseColor("#FFFFFF") -> "others"
                else -> "" // or "None"
            }

            val gender = MultipartUtil.stringToRequestBody(selectedGender)
            LoadingUtils.showDialog(requireContext(),false)

            viewModel.updateProfile(
                fullName, height, weight,age,gender,imageProfileMultiPart,state
            ).collect{ it ->
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        SessionManager(requireContext()).setUserName(binding.etName.text.toString())
                        binding.etFullName.setText(binding.etName.text.toString())
                        SessionManager(requireContext()).setUserState(selectedState)
                        binding.llEditDelete.visibility =View.VISIBLE
                        binding.llSaveCancel.visibility =View.GONE
                        binding.textView11.setText(R.string.height)
                        binding.textView12.setText(R.string.weight)
                        Log.d("testing_profile_name", binding.etName.text.toString() )
                        SessionManager(requireContext()).setUserName(binding.etName.text.toString())
//                        val parts = it.data?.split("-----")
//                        val path = (parts?.get(1) ?: "")
                        originalProfileData = it.data
                        originalProfileData?.let {
                            SessionManager(requireContext()).setUserImage(AppConstant.Base_URL+it.profile_path)
                            settingDataToUi(it)
                            disableAllEdlitText()
                            dialogACCOUNTSuccess()
                            binding.etState.isEnabled = false
                            binding.etState.isClickable = false
                        }
//                        SessionManager(requireContext()).setUserImage(path)
//                        Log.d("TESTING_PROFILE_SAVE",path.toString())


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

    private fun enableAllEdlitText(){
        binding.apply {
            etName.isEnabled = true
            etHeight.isEnabled = true
            etweight.isEnabled = true
            etAge.isEnabled = true
            binding.etState.isEnabled = true
            binding.etState.isClickable = true
            editIcon.visibility = View.VISIBLE
        }
    }

    private fun disableAllEdlitText(){
        binding.apply {
            etName.isEnabled = false
            etHeight.isEnabled = false
            etweight.isEnabled = false
            etAge.isEnabled = false
            binding.etState.isEnabled = false
            binding.etState.isClickable = false
            editIcon.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogDeleteAccount() {
        val dialog = Dialog(requireContext())
        val binding = DialogLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            icon.setImageResource(R.drawable.delete_ic)
            tvDescription.text = "Delete Account"
            description2.text = " Are you sure you want to delete your account?"
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNo.setOnClickListener { dialog.dismiss() }
            btnYes.setOnClickListener {
                lifecycleScope.launch {
                    LoadingUtils.showDialog(requireContext(),false)
                    viewModel.deleteAccount().collect {
                        when(it){
                            is NetworkResult.Success ->{
                                dialog.dismiss()
                                LoadingUtils.hideDialog()
                                var sessionManager = SessionManager(requireContext())
                                sessionManager.clearSession()
                                dialogDeleteSuccess()
                            }
                            is NetworkResult.Error ->{
                                LoadingUtils.hideDialog()
                                LoadingUtils.hideDialog()

                            }
                            else ->{

                            }
                        }
                    }
                }

            }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    private fun dialogACCOUNTSuccess() {
        val dialog = Dialog(requireContext())
        val binding = DialogConfirmAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            tvDescription.text = "Successful"
            description2.text = " Your profile is updated successfully."
            btnClose.setOnClickListener { dialog.dismiss() }
            btnOkay.setOnClickListener { dialog.dismiss() }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    private fun dialogDeleteSuccess() {
        val dialog = Dialog(requireContext())
        val binding = DialogConfirmAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            tvDescription.text = "Successful"
            description2.text = " Your account has been successfully deleted."
            btnClose.setOnClickListener { dialog.dismiss() }
            btnOkay.setOnClickListener { dialog.dismiss() }
        }

        dialog.apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics).toInt()
            window?.setLayout(screenWidth - (2 * marginPx), ViewGroup.LayoutParams.WRAP_CONTENT)
            show()
        }
    }

    private fun openImageChooser() {
        val options = arrayOf("Open Camera", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> {
                    ImagePicker.with(this@ProfileFragment)
                        .crop()         // Optional
                        .compress(1024) // Final image size < 1 MB
                        .galleryOnly()
                        .createIntent { intent ->
                            pickImageLauncher.launch(intent)
                        }
                }
            }
        }
        builder.show()
    }

    private fun openGallery() {
//
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun saveImageUri(uri: String) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(IMAGE_URI_KEY, uri) }
    }

    private fun loadSavedImage() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = prefs.getString(IMAGE_URI_KEY, null)

        uriString?.let {
            val uri = it.toUri()
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.profileImage.setImageBitmap(bitmap) // Use bitmap instead of URI
                imageProfileMultiPart = MultipartUtil.uriToMultipart(requireContext(),uri,"profileImage")

                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri {
        val file = File(requireContext().filesDir, "profile_image.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(file)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE || requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    STORAGE_PERMISSION_REQUEST_CODE -> openGallery()
                    CAMERA_PERMISSION_REQUEST_CODE -> openCamera()
                }
            } else {
                // Show message if permission is denied
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1002
    }

    private fun uriToMultipart(
        context: Context,
        uri: Uri,
        partName: String = "file"
    ): MultipartBody.Part? {
        return if (uri.scheme == "file") {
            // Handle file:// URIs
            val file = File(uri.path!!)
            val mimeType = URLConnection.guessContentTypeFromName(file.name) ?: "application/octet-stream"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, file.name, requestFile)
        } else {
            // Handle content:// URIs
            val contentResolver = context.contentResolver
            val fileName = getFileName(contentResolver, uri) ?: return null
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileBytes = inputStream.use { it.readBytes() }
            val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), fileBytes)
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        }
    }

    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        if (uri.scheme == "file") {
            return File(uri.path!!).name
        }

        var name: String? = null
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
}
