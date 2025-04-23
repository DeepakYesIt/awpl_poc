package com.bussiness.awpl.fragment.basicdetailscreen

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentBasicInfoScreenBinding
import androidx.core.graphics.toColorInt
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.utils.ErrorMessages

class BasicInfoScreen : Fragment() {

    private var _binding: FragmentBasicInfoScreenBinding? = null
    private val binding get() = _binding!!
    private var type: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBasicInfoScreenBinding.inflate(inflater, container, false)

        // Get arguments from bundle
        type = arguments?.getString("TYPE")
        if(type != null && type == "forHome"){
            binding.backIcon.visibility = View.VISIBLE
            binding.backIcon.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        else{
            binding.backIcon.visibility = View.GONE
        }
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViews = listOf(binding.txtMale, binding.txtFemale, binding.txtOthers)

        textViews.forEach { textView ->
            textView.setOnClickListener {
                updateSelection(textView, textViews)
            }
        }
        binding.etHeight.setOnClickListener {
            showHeightPickerDialog()
        }
    }

    private fun clickListener() {
        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                if (type == "forHome") {
                    // Navigate to the second screen if TYPE was passed
                    findNavController().navigate(R.id.scheduledCallConsultation2)
                } else {
                    // Default
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
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

    private fun validateFields(): Boolean {
        binding.apply {
            var isValid = true

            if (etName.text.toString().isEmpty()) {
                etName.error = ErrorMessages.ERROR_NAME
                etName.requestFocus()
                return  false
            }
            if (etHeight.text.toString().isEmpty()) {
                etHeight.error = ErrorMessages.ERROR_HEIGHT
                etHeight.requestFocus()
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





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
