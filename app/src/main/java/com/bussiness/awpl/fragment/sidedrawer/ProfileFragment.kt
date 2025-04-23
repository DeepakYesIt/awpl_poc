package com.bussiness.awpl.fragment.sidedrawer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
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
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.base.CommonUtils
import com.bussiness.awpl.databinding.DialogCancelAppointmentBinding
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogLogoutBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val PREFS_NAME = "ProfilePrefs"
    private val IMAGE_URI_KEY = "profile_image_uri"

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.profileImage.setImageURI(it)
            saveImageUri(it.toString()) // Save URI to SharedPreferences
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val imageUri = saveBitmapToInternalStorage(it) // Save bitmap & get URI
                binding.profileImage.setImageURI(imageUri)
                saveImageUri(imageUri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSavedImage()
        val textViews = listOf(binding.txtMale, binding.txtFemale, binding.txtOthers)

        textViews.forEach { textView ->
            textView.setOnClickListener {
                updateSelection(textView, textViews)
            }
        }
        binding.etHeight.setOnClickListener {
            showHeightPickerDialog()
        }

        clickListener()
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

        AlertDialog.Builder(requireContext())
            .setTitle("Select Height")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val selectedFeet = feetPicker.value
                val selectedInches = inchPicker.value
                binding.etHeight.setText("$selectedFeet ft $selectedInches in")
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
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
           // btnDeleteAccount.setOnClickListener { dialogDeleteAccount() }
            binding.rlEditProfile.setOnClickListener {
                binding.llEditDelete.visibility = View.GONE
                binding.llSaveCancel.visibility =View.VISIBLE
            }
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
                dialog.dismiss()
                dialogDeleteSuccess()
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
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14+
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED), STORAGE_PERMISSION_REQUEST_CODE)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_REQUEST_CODE)
                }
            }
            else -> { // Android 12 and below
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
                }
            }
        }
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
                android.widget.Toast.makeText(requireContext(), "Permission Denied!", android.widget.Toast.LENGTH_SHORT).show()
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
}
