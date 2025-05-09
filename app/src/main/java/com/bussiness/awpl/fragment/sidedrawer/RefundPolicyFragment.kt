package com.bussiness.awpl.fragment.sidedrawer

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.databinding.FragmentRefundPolicyBinding
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.viewmodel.RefundViewModel
import com.bussiness.awpl.viewmodel.TermConditionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RefundPolicyFragment : Fragment() {

    private var _binding: FragmentRefundPolicyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RefundViewModel by lazy {
        ViewModelProvider(this)[RefundViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.webView.settings.javaScriptEnabled = true

    //   binding.webView.loadUrl("file:///android_asset/refund_policy.html")

        callingRefundPolicy()

    }


    private fun callingRefundPolicy(){
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.refund().collect{
                when(it){
                    is NetworkResult.Success ->{
                         LoadingUtils.hideDialog()
                        val htmlString = it.data
                        val plainText = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString()
                        binding.tvRefund.setText(plainText)
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hideDialog()
                     LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }else ->{

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
