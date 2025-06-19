package com.bussiness.awpl.fragment.bookappointment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.PaytmActivity
import com.bussiness.awpl.adapter.SummaryAdapter
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogCongratulationsBinding
import com.bussiness.awpl.databinding.FragmentSummaryScreenBinding
import com.bussiness.awpl.model.BookingResponseModel
import com.bussiness.awpl.model.PayuPaymentModel
import com.bussiness.awpl.model.SummaryModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class SummaryScreen : Fragment() {

    private var _binding: FragmentSummaryScreenBinding? = null
    private val binding get() = _binding!!
    private  var bookingResponseModel: BookingResponseModel? =null
    private lateinit var adapter : SummaryAdapter
    private  var appointmentId :Int =0
    private var paymentAmount :String =""

    private lateinit var summaryViewModel : SummaryViewModel

    var date :String =""
    var time :String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryScreenBinding.inflate(inflater, container, false)
        summaryViewModel =  ViewModelProvider(this)[SummaryViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        clickListener()

        arguments?.let {
            if(it.containsKey(AppConstant.ID)){
                appointmentId = it.getInt(AppConstant.ID)
                summaryViewModel.appoitmentId = appointmentId
                date = it.getString(AppConstant.DATE).toString()
                summaryViewModel.date = date
                time = it.getString(AppConstant.TIME).toString()
                summaryViewModel.time = time
            }
        }
        bookingResponseModel = arguments?.getParcelable<BookingResponseModel>(AppConstant.BOOK_MODEL)

        binding.textStrikedPrice.text = Html.fromHtml(getString(R.string.striked_price), Html.FROM_HTML_MODE_LEGACY)

        settingDataToUi()
    }

    @SuppressLint("SetTextI18n")
    private fun settingDataToUi(){
        bookingResponseModel?.let { obj->
            Log.d("TESTING_BOOKING",bookingResponseModel.toString())
           if(!obj.is_first_consultation){
               paymentAmount = obj.payment_amount
               binding.textFree.text = "₹ "+ obj.payment_amount
               binding.btnNext.text = "₹ "+ obj.payment_amount +" |"+ " (Pay and Consult)"
               binding.applyPromoConstraintLayout.visibility = View.VISIBLE
               binding.promoHeading.visibility = View.VISIBLE
           }
           else{
               paymentAmount="0.0"
               binding.textFree.text = "Free"
               binding.applyPromoConstraintLayout.visibility = View.GONE
               binding.promoHeading.visibility = View.GONE
           }

         //   adapter.updateAdapter(obj.doctor_list)
            Log.d("TESTING_ANDROID",obj.doctor_list.size.toString())
              adapter.updateAdapter(obj.doctor_list)
        }
    }

    private fun setupRecyclerView() {

         adapter = SummaryAdapter(mutableListOf())
        binding.recyclerViewDoctors.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.recyclerViewDoctors.adapter = adapter


    }

    private fun clickListener() {
        binding.apply {
            btnNext.setOnClickListener {
                if(binding.checkboxPrivacy.isChecked){
                    if(btnNext.text =="Free"){
                      congratsDialog()
                    }else {
                        callingPaymentApi()
                    }
                }else{
                   LoadingUtils.showErrorDialog(requireContext(),"Please agree to the Privacy Policy and Terms & Conditions")
                }
            }
            arrowIcon.setOnClickListener {
                if(edtPromoCode.text.toString().isNotEmpty()){
                    callingPromoCodeApi(edtPromoCode.text.toString())

                }
            }
            txtPrivacyPolicy.setOnClickListener { findNavController().navigate(R.id.privacyPolicyFragment) }
            txtTermsConditions.setOnClickListener { findNavController().navigate(R.id.termsAndConditionFragment) }
        }
    }

    private fun callingPaymentApi(){
        lifecycleScope.launch {

            LoadingUtils.showDialog(requireContext(),false)
            summaryViewModel.initiatePayment(summaryViewModel.appoitmentId,
                    generateTransactionId(summaryViewModel.appoitmentId.toString()),paymentAmount,"medical_prescription",
                    SessionManager(requireContext()).getUserName(),SessionManager(requireContext()).getUserEmail(),""
                ).collect{

                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hideDialog()
                            var data = it.data
                            openActivityWithUser(data)
                        }

                        is NetworkResult.Error ->{
                            LoadingUtils.hideDialog()
                        }
                        else ->{

                        }
                    }


            }

        }
    }

    private fun callingPromoCodeApi(promoCode: String) {
        lifecycleScope.launch {
              LoadingUtils.showDialog(requireContext(),false)
            summaryViewModel.applyPromoCode(promoCode, appointmentId).collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        binding.applyPromoConstraintLayout.visibility = View.GONE
                        binding.promoValidate.visibility = View.VISIBLE
                        it.data?.final_amount?.let {
                            paymentAmount = it.toString()
                            val value = "₹ $it"
                            binding.textFree.text = value
                        }
                        it.data?.original_amount?.let {
                            val value = "₹ $it"
                            binding.textStrikedPrice.text = value
                        }

                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else->{

                    }
                }
            }


        }
    }

    private fun congratsDialog(){
        val dialog = Dialog(requireContext())
        val binding = DialogCongratulationsBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.homeFragment)
            }
            btnNext.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.homeFragment)
            }
            textView36.setPaintFlags(textView36.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }

    //    binding.textView36.paintFlags = binding.textView36.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

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


    private fun confirmDialog(){
        val dialog = Dialog(requireContext())
        val binding = DialogConfirmAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.homeFragment)
            }
            btnOkay.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.homeFragment)
            }

            description2.text = "Your appointment is confirmed for ${summaryViewModel.date} at ${summaryViewModel.time}."
        }

        //    binding.textView36.paintFlags = binding.textView36.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

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


    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultMessage = data?.getStringExtra("result_key")
             Log.d("TESTING_PAYMENT","Result: $resultMessage")
            if(resultMessage == "Success"){
                confirmDialog()
            }else{
                LoadingUtils.showErrorDialog(requireContext(),"Payment Failed!!")
            }

        }
    }

    private fun openActivityWithUser(data: PayuPaymentModel?) {

        val intent = Intent(requireContext(), PaytmActivity::class.java).apply {
            putExtra("user_data", data)
        }
        activityLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateTransactionId(appointmentId: String): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val uuidPart = UUID.randomUUID().toString().take(8).uppercase()
        return "TXN_${appointmentId}_$timestamp$uuidPart"
    }
}
