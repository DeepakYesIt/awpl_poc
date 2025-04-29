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
import com.bussiness.awpl.databinding.FragmentTermsAndConditionBinding
import com.bussiness.awpl.utils.LoadingUtils
import com.bussiness.awpl.viewmodel.LoginViewModel
import com.bussiness.awpl.viewmodel.TermConditionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TermsAndConditionFragment : Fragment() {

    private var _binding: FragmentTermsAndConditionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TermConditionViewModel by lazy {
        ViewModelProvider(this)[TermConditionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsAndConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         callingTermConditionApi()
    }


    private fun callingTermConditionApi(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.termCondition().collect{
                when(it){
                    is NetworkResult.Success ->{
                        val plainText = Html.fromHtml(it.data, Html.FROM_HTML_MODE_LEGACY).toString()
                        binding.tvPrivacyPolicy.setText(plainText)
                        LoadingUtils.hideDialog()
                    }
                    is NetworkResult.Error ->{
                         LoadingUtils.hideDialog()
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
