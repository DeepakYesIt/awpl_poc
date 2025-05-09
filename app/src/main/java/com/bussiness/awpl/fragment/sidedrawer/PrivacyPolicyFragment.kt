package com.bussiness.awpl.fragment.sidedrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.databinding.FragmentPrivacyPolicyBinding
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.viewmodel.PrivacyViewModel
import com.bussiness.awpl.viewmodel.RefundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyFragment : Fragment() {

    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrivacyViewModel by lazy {
        ViewModelProvider(this)[PrivacyViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callingPrivacyPolicyApi()
    }


    private fun callingPrivacyPolicyApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.privacyPolicy().collect{
                when(it){
                    is NetworkResult.Success->{
                        LoadingUtils.hideDialog()
                        val htmlString = it.data.toString()
                        val plainText = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString()
                        binding.tvPrivacyPolicy.setText(plainText)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
