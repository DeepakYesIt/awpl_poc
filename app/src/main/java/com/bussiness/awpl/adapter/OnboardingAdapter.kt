package com.bussiness.awpl.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bussiness.awpl.databinding.OnboardingItemBinding
import com.bussiness.awpl.model.OnboardingItem
import androidx.core.graphics.toColorInt

class OnboardingAdapter(private val list: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(val binding: OnboardingItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = OnboardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            onboardImage.setImageResource(item.imageRes)

            // Apply styling to text
            description.text = getStyledText(item.description)
        }
    }

    private fun getStyledText(text: String): SpannableString {
        val spannable = SpannableString(text)
        val wordsToHighlight = listOf("First Free Consultation!", "Virtually", "Start Exploring the App Today!")

        wordsToHighlight.forEach { word ->
            val startIndex = text.indexOf(word)
            if (startIndex != -1) {
                spannable.setSpan(
                    ForegroundColorSpan("#199FD9".toColorInt()), // Blue color
                    startIndex,
                    startIndex + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD), // Bold style
                    startIndex,
                    startIndex + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannable
    }
}
