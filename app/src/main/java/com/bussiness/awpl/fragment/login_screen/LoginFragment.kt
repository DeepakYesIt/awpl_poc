package com.bussiness.awpl.fragment.login_screen

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentWelcome3Binding
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentWelcome3Binding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                if(validations()){
                    findNavController().navigate(R.id.basicInfoScreen)
                    sessionManager.saveLoginState(true)
                }
            }
            eyeIcon.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    eyeIcon.setImageResource(R.drawable.show_pass_ic)
                } else {
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    eyeIcon.setImageResource(R.drawable.hide_pas_ic)
                }

                // Move cursor to the end after changing input type
                passwordEditText.setSelection(passwordEditText.text.length)
            }
        }
    }

    private fun validations(): Boolean {
        binding.apply {
            var isValidate = true

            if (passwordEditText.text.toString().trim().isEmpty()) {
                passwordEditText.error = ErrorMessages.ERROR_PASSWORD
                isValidate = false
            }
            if (DSCOdeEditTxt.text.toString().trim().isEmpty()) {
                DSCOdeEditTxt.error = ErrorMessages.ERROR_DSCODE
                isValidate = false
            }
            return isValidate
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
