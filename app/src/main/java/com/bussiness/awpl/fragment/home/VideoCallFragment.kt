package com.bussiness.awpl.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bussiness.awpl.R
import com.bussiness.awpl.databinding.FragmentVideoCallBinding
import com.bussiness.awpl.viewmodel.LoginViewModel
import com.bussiness.awpl.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoCallFragment : Fragment() {

    private var _binding: FragmentVideoCallBinding? = null
    private val binding get() = _binding!!

    private val videoViewModel: VideoViewModel by lazy {
        ViewModelProvider(this)[VideoViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoCallBinding.inflate(inflater, container, false)
        return binding.root


    }


}