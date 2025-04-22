package com.bussiness.awpl.fragment.basicdetailscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentFrgamentBasicInfoInnerBinding


class FrgamentBasicInfoInnerFragment : Fragment() {


    lateinit var binding :FragmentFrgamentBasicInfoInnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentFrgamentBasicInfoInnerBinding.inflate(LayoutInflater.from(requireContext()))


        return binding.root
    }

}