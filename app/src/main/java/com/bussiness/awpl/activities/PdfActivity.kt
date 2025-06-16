package com.bussiness.awpl.activities

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bussiness.awpl.R
import com.bussiness.awpl.utils.AppConstant

class PdfActivity : AppCompatActivity() {

    var pdfUrl :String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent?.let {
            if(it.hasExtra(AppConstant.PDF)){
                pdfUrl = it.getStringExtra(AppConstant.PDF).toString()
                Log.d("TESTING_PDF",pdfUrl)
            }
        }

        val webView = findViewById<WebView>(R.id.webView)

       // replace with your actual URL
        if(pdfUrl.isNotEmpty()) {
            pdfUrl =AppConstant.Base_URL+ pdfUrl
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
            Log.d("TESTING_PDF",googleDocsUrl)
            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.webViewClient = WebViewClient()

            webView.loadUrl(googleDocsUrl)
        }
    }
}