package com.bussiness.awpl.fragment.resource

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.FAQAdapter
import com.bussiness.awpl.databinding.FragmentFAQBinding
import com.bussiness.awpl.model.FAQItem

class FAQFragment : Fragment() {

    private var _binding: FragmentFAQBinding? = null
    private val binding get() = _binding!!
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFAQBinding.inflate(inflater, container, false)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup FAQ RecyclerView
        val faqList = listOf(
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            FAQItem("Lorem ipsum dolor sit amet?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
        )

        faqAdapter = FAQAdapter(faqList)
        binding.faqRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.faqRecyclerview.adapter = faqAdapter
        binding.searchView.queryHint = "Enter your keyword"

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false

// Step 2: Access the EditText inside SearchView and fix hint color (if needed)
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setHintTextColor(Color.GRAY) // You can use ContextCompat.getColor(...) too
        searchEditText.setTextColor(Color.BLACK) // Just in case text also invisible

    }

    private fun clickListeners(){
        binding.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
