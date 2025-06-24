package com.bussiness.awpl.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.ActivityBasicInfoBinding
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.BasicInfoViewModel
import kotlinx.coroutines.launch

class BasicInfoActivity : AppCompatActivity() {

    private lateinit var binding :ActivityBasicInfoBinding
    private var type: String? = null
    private var selectedGender :String =""
    private var diseaseId :Int =0

    private val basicInfoViewModel: BasicInfoViewModel by lazy {
        ViewModelProvider(this)[BasicInfoViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBasicInfoBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clickListener()

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

                    callingBasicInfoApi()
                }
            }

    }

    private fun callingBasicInfoApi(){
        lifecycleScope.launch {
            selectedGender = when {
                binding.txtMale.currentTextColor == Color.parseColor("#FFFFFF") -> "male"
                binding.txtFemale.currentTextColor == Color.parseColor("#FFFFFF") -> "female"
                binding.txtOthers.currentTextColor == Color.parseColor("#FFFFFF") -> "others"
                else -> "" // or "None"
            }
            Log.d("TESTING","Selected Gender "+selectedGender)
            LoadingUtils.showDialog(this@BasicInfoActivity,false)
            //  name: String, height: String, weight: String, age: String, gender: String
            basicInfoViewModel.basicInfo(binding.etName.text.toString(),binding.etHeight.text.toString(),
                binding.etweight.text.toString(),binding.etAge.text.toString(),selectedGender
            ).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showSuccessDialog(this@BasicInfoActivity,it.data.toString()){
                            // Default
                            val intent = Intent(this@BasicInfoActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.showErrorDialog(this@BasicInfoActivity,it.message.toString())
                    }
                    else ->{

                    }
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

        val dialog = AlertDialog.Builder(this)
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
                textView.setTextColor(ContextCompat.getColor(this, R.color.greyColor))
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
                LoadingUtils.showErrorDialog(this@BasicInfoActivity,"Height selection is required")
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
            if(etAge.text.toString().toInt() <=12){
                etAge.error ="Age Should be more than 12 years"
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
}