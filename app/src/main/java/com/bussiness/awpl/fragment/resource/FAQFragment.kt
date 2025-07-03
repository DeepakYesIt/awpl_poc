package com.bussiness.awpl.fragment.resource

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.R
import com.bussiness.awpl.activities.HomeActivity
import com.bussiness.awpl.adapter.FAQAdapter
import com.bussiness.awpl.databinding.FragmentFAQBinding
import com.bussiness.awpl.model.FAQItem
import com.bussiness.awpl.utils.LoadingUtils

import com.bussiness.awpl.viewmodel.FaqViewModel
import com.bussiness.awpl.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import androidx.appcompat.widget.SearchView

@AndroidEntryPoint
class FAQFragment : Fragment() {

    private var _binding: FragmentFAQBinding? = null
    private val binding get() = _binding!!
    private lateinit var faqAdapter: FAQAdapter
    private var faqList: List<FAQItem> = mutableListOf()
    var filteredList: MutableList<FAQItem> = mutableListOf()


    private val viewModel: FaqViewModel by lazy {
        ViewModelProvider(this)[FaqViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFAQBinding.inflate(inflater, container, false)
        searchFunctionality()
        return binding.root
    }

    private fun searchFunctionality(){

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optional: handle search action on keyboard submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFAQ(newText.orEmpty())
                return true
            }
        })

    }

    private fun filterFAQ(query: String) {
        val lowerQuery = query.lowercase()

        filteredList.clear()
        if (lowerQuery.isEmpty()) {
            filteredList.addAll(faqList)
            if(filteredList.size ==0){
                binding.tvNoDataView.visibility =View.VISIBLE
            }else{
                binding.tvNoDataView.visibility =View.GONE
            }
            faqAdapter.updateAdapter(filteredList)
        } else {
            filteredList.addAll(
                faqList.filter {

                    it.question.lowercase().contains(lowerQuery) || it.answer.lowercase().contains(lowerQuery)
                }
            )

            if(filteredList.size ==0){
                binding.tvNoDataView.visibility =View.VISIBLE
            }else{
                binding.tvNoDataView.visibility =View.GONE
            }
            faqAdapter.updateAdapter(filteredList)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        searchEditText.setBackgroundColor(Color.TRANSPARENT)
        searchEditText.background = null

        callingFaqApi()
    }

    private fun callingFaqApi() {
        lifecycleScope.launch {
            LoadingUtils.showDialog(requireContext(),false)
            viewModel.faq().collect{
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hideDialog()
                        it.data?.let {
                            it1 ->
                             faqAdapter.updateAdapter(it1)
                             faqList = it1
                        }
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

    private fun clickListeners(){
        binding.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
