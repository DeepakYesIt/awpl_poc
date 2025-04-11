package com.bussiness.awpl.fragment.schedule

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.DialogConfirmAppointmentBinding
import com.bussiness.awpl.databinding.DialogLogoutBinding
import com.bussiness.awpl.databinding.FragmentCancelAppointmentBinding

class CancelAppointment : Fragment() {

    private var _binding: FragmentCancelAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: HomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.apply {
            submitBtn.setOnClickListener {
                cancelDialog()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cancelDialog() {
        val dialog = Dialog(requireContext())
        val binding = DialogConfirmAppointmentBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.apply {
            tvTitle.setImageResource(R.drawable.sucess_icon)
            tvDescription.text = "Appointment cancelled"
            description2.text = "Your appointment is successfully cancelled."
            btnClose.setOnClickListener { dialog.dismiss() }
            btnOkay.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.scheduleFragment) }
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
