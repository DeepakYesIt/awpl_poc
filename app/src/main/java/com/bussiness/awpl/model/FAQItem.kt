package com.bussiness.awpl.model

data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
