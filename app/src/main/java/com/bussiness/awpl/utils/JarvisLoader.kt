package com.bussiness.awpl.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle

import android.widget.LinearLayout
import com.bussiness.awpl.R


class JarvisLoader(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shimmer_loading_dialog)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(R.color.wallet_dim_foreground_disabled_holo_dark)
    }


}