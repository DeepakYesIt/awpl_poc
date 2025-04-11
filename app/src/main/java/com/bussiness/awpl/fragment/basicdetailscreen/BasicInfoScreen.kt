package com.bussiness.awpl.fragment.basicdetailscreen

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicInfoScreenBinding.inflate(inflater, container, false)

        // Get arguments from bundle
        type = arguments?.getString("TYPE")
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
                isValid = false
            }
            if (etHeight.text.toString().isEmpty()) {
                etHeight.error = ErrorMessages.ERROR_HEIGHT
                isValid = false
            }
            if (etweight.text.toString().isEmpty()) {
                etweight.error = ErrorMessages.ERROR_WEIGHT
                isValid = false
            }
            if (etAge.text.toString().isEmpty()) {
                etAge.error = ErrorMessages.ERROR_AGE
                isValid = false
            }

            val selectedGender = listOf(txtMale, txtFemale, txtOthers).any {
                it.currentTextColor == "#FFFFFF".toColorInt()
            }
            if (!selectedGender) {
                txtOthers.error = ErrorMessages.ERROR_GENDER
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
