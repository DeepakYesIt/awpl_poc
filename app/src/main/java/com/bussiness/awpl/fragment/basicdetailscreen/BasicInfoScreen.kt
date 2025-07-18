package com.bussiness.awpl.fragment.basicdetailscreen


import android.graphics.Color
import androidx.appcompat.app.AlertDialog

import android.content.Intent

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentBasicInfoScreenBinding
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.StateAdapter
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.SessionManager

import com.bussiness.awpl.viewmodel.BasicInfoViewModel
import com.bussiness.awpl.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasicInfoScreen : Fragment() {

    private var _binding: FragmentBasicInfoScreenBinding? = null
    private val binding get() = _binding!!
    private var type: String? = null
    private var selectedGender :String =""
    private var diseaseId :Int =0
    private var back :Boolean = false
    private var selectedState :String =""

    private val basicInfoViewModel: BasicInfoViewModel by lazy {
        ViewModelProvider(this)[BasicInfoViewModel::class.java]

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBasicInfoScreenBinding.inflate(inflater, container, false)

        Log.d("TESTING_BASIC","HERE INSIDE THE BASIC INFO SCREEN")
        // Get arguments from bundle
        type = arguments?.getString("TYPE")
        arguments?.let {
            if(it.containsKey(AppConstant.DISEASE_ID)){
                diseaseId = it.getInt(AppConstant.DISEASE_ID)
                binding.tvState.visibility =View.GONE
                binding.etState.visibility =View.GONE

            }
            if(it.containsKey(AppConstant.BACK)){
                back = true
            }
        }
        SessionManager(requireContext()).getUserName()?.let { Log.d("TESTING_NAME", "iNSIDE "+it) }





        if(type != null && type == "forHome"){

            binding.backIcon.visibility = View.VISIBLE

            binding.backIcon.setOnClickListener {
                if(back){
                 findNavController().navigate(R.id.homeFragment)
                }
                else {

                    findNavController().navigateUp()
                }
            }
            binding.textView11.text ="Enter Height"
            binding.textView12.text ="Enter Weight (Kg)"
            binding.textView9.text ="Please enter their basic information below"
        }
        else{
            binding.backIcon.visibility = View.GONE
            if(!back){
                binding.etName.setText(SessionManager(requireContext()).getUserName())
            }
        }

        val recyclerView = binding.stateRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = StateAdapter(AppConstant.statesAndUTs) { it ->
              binding.etState.setText(it)
              selectedState= it
              binding.myCard.visibility =View.GONE
        }

        clickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViews = listOf(binding.txtMale, binding.txtFemale, binding.txtOthers)

        textViews.forEach {
            textView -> textView.setOnClickListener {
                updateSelection(textView, textViews)
            }
        }

        binding.etHeight.setOnClickListener {
            showHeightPickerDialog()
        }

        selectedState = SessionManager(requireContext()).getUserState()

        AppConstant.statesAndUTs.forEach {
            if(it.equals(selectedState,ignoreCase = true)){
                selectedState = it
                return@forEach
            }
        }


        binding.etState.setText(selectedState)

    }

    override fun onResume() {
        super.onResume()
        binding.etName.setText("")
        binding.etweight.setText("")
        binding.etAge.setText("")
        if(type != "forHome"){
            binding.etName.setText(SessionManager(requireContext()).getUserName())
        }
    }

    private fun clickListener() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle back press here
                    // Example: navigate up or show a dialog
                    if (back) {
                      findNavController().navigate(R.id.homeFragment)
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        )

        binding.etState.setOnClickListener {
            if(binding.myCard.isVisible){
                binding.myCard.visibility =View.GONE
            }else{
                binding.myCard.visibility = View.VISIBLE
            }
        }

        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                if (type == "forHome") {
                    var bundle = Bundle().apply {
                        selectedGender = when {
                            binding.txtMale.currentTextColor == Color.parseColor("#FFFFFF") -> "male"
                            binding.txtFemale.currentTextColor == Color.parseColor("#FFFFFF") -> "female"
                            binding.txtOthers.currentTextColor == Color.parseColor("#FFFFFF") -> "others"
                            else -> "" // or "None"
                        }
                        putString(AppConstant.Age,binding.etAge.text.toString())
                        putString(AppConstant.Height,binding.etHeight.text.toString())
                        putString(AppConstant.Weight,binding.etweight.text.toString())
                        putInt(AppConstant.DISEASE_ID,diseaseId)
                        putString(AppConstant.NAME,binding.etName.text.toString())

                        putString(AppConstant.Gender, selectedGender)
                    }

                    findNavController().navigate(R.id.scheduledCallConsultation2,bundle)
                }else{
                    callingBasicInfoApi()
                }
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
               LoadingUtils.showDialog(requireContext(),false)
          //  name: String, height: String, weight: String, age: String, gender: String
            basicInfoViewModel.basicInfo(binding.etName.text.toString(),binding.etHeight.text.toString(),
                binding.etweight.text.toString(),binding.etAge.text.toString(),selectedGender,selectedState
                ).collect{
                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hideDialog()
                            SessionManager(requireContext()).setUserName(binding.etName.text.toString())
                              LoadingUtils.showSuccessDialog(requireContext(),it.data.toString()){
                                  val intent = Intent(requireContext(), HomeActivity::class.java)
                                  startActivity(intent)
                              }
                        }
                        is NetworkResult.Error ->{
                                LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
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

       val builder = AlertDialog.Builder(requireContext())
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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#FFC107"))
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
                return  false
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
            if(etAge.text.toString().toInt() <12 || etAge.text.toString().toInt() > 120){
                etAge.error ="Age should be more than 11 years and less than 120 years."
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
