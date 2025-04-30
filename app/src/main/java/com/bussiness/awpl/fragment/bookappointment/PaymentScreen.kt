package com.bussiness.awpl.fragment.bookappointment

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogLogoutBinding
import com.bussiness.awpl.databinding.FragmentPaymentScreenBinding
import com.bussiness.awpl.databinding.FragmentSummaryScreenBinding
import com.razorpay.Checkout


class PaymentScreen : Fragment() {
    private var _binding: FragmentPaymentScreenBinding? = null
    private val binding get() = _binding!!
    private var isPaytmSelected = false
    private var isRazorpaySelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentScreenBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore the state after the fragment is created or recreated

        restoreSelection()

        clickListener()
    }

    private fun restoreSelection() {
        binding.apply {
            if (isPaytmSelected) {
                radioPaytm.isChecked = true
                radioRazorpay.isChecked = false
            } else if (isRazorpaySelected) {
                radioPaytm.isChecked = false
                radioRazorpay.isChecked = true
            }

            updatePayButton() // Update the Pay button state based on the selected option
        }
    }

    private fun clickListener() {
        binding.apply {
            fun selectPaytm() {
                isPaytmSelected = true
                isRazorpaySelected = false
                radioPaytm.isChecked = true
                radioRazorpay.isChecked = false
                updatePayButton()
            }

            fun selectRazorpay() {
                isRazorpaySelected = true
                isPaytmSelected = false
                radioRazorpay.isChecked = true
                radioPaytm.isChecked = false
                updatePayButton()
            }

            llPaytm.setOnClickListener { selectPaytm() }
            llRazorpay.setOnClickListener { selectRazorpay() }

            radioPaytm.setOnClickListener { selectPaytm() }
            radioRazorpay.setOnClickListener { selectRazorpay() }

            btnPay.setOnClickListener {
                if (isPaytmSelected || isRazorpaySelected) {
                    congratsDialog()
                }
            }

            refundPolicyTxt.setOnClickListener {
                findNavController().navigate(R.id.refundPolicyFragment)
            }
        }
    }

    private fun updatePayButton() {
        binding.apply {
            val isOptionSelected = radioPaytm.isChecked || radioRazorpay.isChecked
            btnPay.isEnabled = isOptionSelected
            btnPay.setBackgroundResource(
                if (isOptionSelected) R.drawable.update_pay_bg else R.drawable.pay_btn_bg
            )
        }
    }

    private fun congratsDialog() {
        val dialog = Dialog(requireContext())
        val binding = DialogConfirmAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            btnClose.setOnClickListener { dialog.dismiss() }
            btnOkay.setOnClickListener {
                findNavController().navigate(R.id.homeFragment)
                dialog.dismiss()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
