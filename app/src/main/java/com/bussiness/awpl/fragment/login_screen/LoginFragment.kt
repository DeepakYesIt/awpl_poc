package com.bussiness.awpl.fragment.login_screen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.FragmentWelcome3Binding
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.ErrorMessages
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.utils.LoadingUtils.Companion.ensurePeriod

import com.bussiness.awpl.utils.MultipartUtil
import com.bussiness.awpl.utils.SessionManager
import com.bussiness.awpl.viewmodel.LoginViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding: FragmentWelcome3Binding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false
    private var token :String =""
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWelcome3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        gettingToken()
    }

    private fun clickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                if(validations()){
                    loginApi()
                }
            }

            eyeIcon.setOnClickListener {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    eyeIcon.setImageResource(R.drawable.show_pass_ic)
                }
                else {
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    eyeIcon.setImageResource(R.drawable.hide_pas_ic)
                }

                // Move cursor to the end after changing input type
                passwordEditText.setSelection(passwordEditText.text.length)
            }
        }
    }

    private fun gettingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("AWPL_TOKEN", token)
            // Log or send the token to your server
        }
    }


    private fun loginApi(){
//        LoaderManager.showDialog(requireContext(), isCancelable = false)

        LoadingUtils.showDialog(requireContext(),false)

        lifecycleScope.launch {
            loginViewModel.login(binding.DSCOdeEditTxt.text.toString() , binding.passwordEditText.text.toString() , token,"android").collect{

                when(it){
                    is NetworkResult.Success ->{
                        val data = it.data
                        val sessionManager = SessionManager(requireContext())
                        data?.token?.let { it1 ->
                            Log.d("TESTING_TOKEN","TOKEN ISNID  "+it1)
                            sessionManager.setAuthToken(it1) }
                        data?.userId?.let { it1-> sessionManager.setUserId(it1)}
                        data?.name?.let { it1-> sessionManager.setUserName(it1) }
                        data?.email?.let { it1->sessionManager.setUserEmail(it1) }
                        data?.state?.let { it1-> sessionManager.setUserState(it1) }

                        data?.profile_path?.let {
                            it1-> sessionManager.setUserImage(AppConstant.Base_URL+ MultipartUtil.ensureStartsWithSlash(it1))
                        }

                        Log.d("TESTING_NAME",sessionManager.getUserName().toString())
                        LoadingUtils.hideDialog()

                        sessionManager.saveLoginState(true)




                        if(data?.basic_information ==0) {
                            findNavController().navigate(R.id.basicInfoScreen)
                            sessionManager.saveLoginState(true)
                        }
                        else{
                            val intent = Intent(requireContext(),HomeActivity::class.java)
                            startActivity(intent)
                        }

                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()

                        showErrorDialog(requireContext(),it.message.toString())

                    }
                    else ->{
                    }
                }
            }
        }
    }


    fun showErrorDialog(context: Context?, text: String) {

        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_error, null)

        // Find views
        val errorMessage = dialogView.findViewById<TextView>(R.id.text)
        //  val errorIcon = dialogView.findViewById<ImageView>(R.id.errorIcon)
        val okButton = dialogView.findViewById<TextView>(R.id.textOkayButton)
        val cancelBtn = dialogView.findViewById<ImageView>(R.id.imageCross)

        // Set the error message
        errorMessage.text = ensurePeriod(text)

        // Create the dialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()




        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        // Set button click listener
        okButton.setOnClickListener {
            dialog?.dismiss()
        }

        // Show the dialog
        if (context == null) return
        if(text.contains("awplconnect@asclepiuswellness.co.in") == true){
            var index = text.indexOf(":")
            var substr = text.substring(0,index+1) +"\n"+ text.substring(index+1 ,text.length)
            val email = "awplconnect@asclepiuswellness.co.in"
            val spannable = SpannableString(substr)

            val startIndex = substr.indexOf(email)
            val endIndex = startIndex + email.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    widget.context.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLUE   // Set link color
                    ds.isUnderlineText = true // Optional: remove underline
                }
            }

            spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            errorMessage.text = spannable
            errorMessage.movementMethod = LinkMovementMethod.getInstance()
            errorMessage.highlightColor = Color.TRANSPARENT
        }
        dialog?.show()
    }

    private fun validations(): Boolean {
        binding.apply {
            var isValidate = true

            if (passwordEditText.text.toString().trim().isEmpty()) {
                passwordEditText.error = ErrorMessages.ERROR_PASSWORD
                passwordEditText.requestFocus()
                isValidate = false
                return false
            }
            if (DSCOdeEditTxt.text.toString().trim().isEmpty()) {
                DSCOdeEditTxt.error = ErrorMessages.ERROR_DSCODE
                DSCOdeEditTxt.requestFocus()
                isValidate = false
                return false
            }
            return isValidate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
