package com.bussiness.awpl.fragment.bookappointment

import android.app.Dialog
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.adapter.SummaryAdapter
import com.bussiness.awpl.databinding.DialogCongratulationsBinding
import com.bussiness.awpl.databinding.FragmentSummaryScreenBinding
import com.bussiness.awpl.model.BookingResponseModel
import com.bussiness.awpl.model.SummaryModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SummaryScreen : Fragment() {

    private var _binding: FragmentSummaryScreenBinding? = null
    private val binding get() = _binding!!
    private  var bookingResponseModel: BookingResponseModel? =null
    private lateinit var adapter : SummaryAdapter
    private  var appointmentId :Int =0
    private lateinit var summaryViewModel : SummaryViewModel

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
            }
        }
        bookingResponseModel = arguments?.getParcelable<BookingResponseModel>(AppConstant.BOOK_MODEL)

        binding.textStrikedPrice.text = Html.fromHtml(getString(R.string.striked_price), Html.FROM_HTML_MODE_LEGACY)

        settingDataToUi()
    }

    private fun settingDataToUi(){
        bookingResponseModel?.let { obj->
            Log.d("TESTING_BOOKING",bookingResponseModel.toString())
           if(!obj.is_first_consultation){
               binding.textFree.text = "₹ "+obj.payment_amount.toString()
               binding.btnNext.text = "₹ "+obj.payment_amount.toString()+" |"+ " (Pay and Consult)"
           }
           else{
               binding.textFree.text = "Free"
           }

         //   adapter.updateAdapter(obj.doctor_list)
            Log.d("TESTING_ANDROID",obj.doctor_list.size.toString())
              adapter.updateAdapter(obj.doctor_list)
        }
    }

    private fun setupRecyclerView() {
        val doctorList = listOf(
            SummaryModel(R.drawable.doctor_image, "Dr. Aman Sharma", "Experience: 5 Years"),
            SummaryModel(R.drawable.doctor_image, "Dr. Priya Mehta", "Experience: 8 Years"),
            SummaryModel(R.drawable.doctor_image, "Dr. Rahul Verma", "Experience: 10 Years")
        )

         adapter = SummaryAdapter(mutableListOf())
        binding.recyclerViewDoctors.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.recyclerViewDoctors.adapter = adapter


    }

    private fun clickListener() {
        binding.apply {
            btnNext.setOnClickListener { congratsDialog() }
            arrowIcon.setOnClickListener {
                if(edtPromoCode.text.toString().isNotEmpty()){
                    callingPromoCodeApi(edtPromoCode.text.toString())

                }
            }
            txtPrivacyPolicy.setOnClickListener { findNavController().navigate(R.id.privacyPolicyFragment) }
            txtTermsConditions.setOnClickListener { findNavController().navigate(R.id.termsAndConditionFragment) }
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
            btnClose.setOnClickListener { dialog.dismiss() }
            btnNext.setOnClickListener { findNavController().navigate(R.id.paymentScreen)
                dialog.dismiss()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
