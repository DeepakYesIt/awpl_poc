package com.bussiness.awpl.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bussiness.awpl.R
import com.bussiness.awpl.model.PayuPaymentModel
import com.google.gson.JsonObject
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener

import org.json.JSONObject

class PaytmActivity : AppCompatActivity() {

    var amt :String =""

    private lateinit var webView: WebView
    private lateinit var paymentUrl: String
    private lateinit var postParams: Map<String, String>
    var key =""
    var tranxId :String =""
    var amount :String =""
    var productInfo :String =""
    var firstName :String =""
    var hash :String =""
    var sUrl :String =""
    var lUrl :String =""
    var email :String? = null
    var phone :String? =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paytm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
     //   callPaymentTaskPayU()
        //  paymentWork()
        webView = findViewById(R.id.webView)
        setupWebView()

        // Ye data backend se pass hua maan ke chal rahe hain
      //  paymentUrl = "https://secure.payu.in/_payment" // Production URL
        paymentUrl = "https://sandbox.payu.in/_payment" // Production URL

        val user = intent.getSerializableExtra("user_data") as? PayuPaymentModel
        user?.let {
            key = it.key.toString()
            tranxId = it.txnid.toString()
            amount = it.amount.toString()
            productInfo = it.productinfo.toString()
             it.firstname?.let {  firstName = it}
            it.hash?.let { hash =it }
            it.surl?.let {  sUrl = it }
            it.furl?.let { lUrl = it }
            it.email?.let { email =it }

            Log.d("TESTING_PAYMENT_KEY","key ${it.key}")
            Log.d("TESTING_PAYMENT_KEY","txnid ${it.txnid}")
            Log.d("TESTING_PAYMENT_KEY","amount ${it.amount}")
            Log.d("TESTING_PAYMENT_KEY","productinfo ${it.productinfo}")
            Log.d("TESTING_PAYMENT_KEY","firstName ${it.firstname}")
            Log.d("TESTING_PAYMENT_KEY","hash ${it.hash}")
            Log.d("TESTING_PAYMENT_KEY","surl ${it.surl}")
            Log.d("TESTING_PAYMENT_KEY","furl ${it.furl}")
            Log.d("TESTING_PAYMENT_KEY","email ${it.email}")
        }

        val postParams: Map<String, String> = mapOf(
            "key" to key.toString(),
            "txnid" to tranxId.toString(),
            "amount" to amount.toString(),
            "productinfo" to productInfo.toString(),
            "firstname" to firstName.toString(),
            "email" to email.toString(),
            "phone" to "",
            "surl" to sUrl.toString(),
            "furl" to lUrl.toString(),
            "hash" to hash.toString()
        )
        // POST request WebView me load karo
        val postData = buildPostData(postParams)
        webView.postUrl(paymentUrl, postData.toByteArray(Charsets.UTF_8))
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Loading indicator dikhana agar chahiye
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Success or failure URL detect karo
                url?.let {
                    if (it.contains("success")) {

                        val resultIntent = Intent().apply {
                            putExtra("result_key", "Success")
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
//                        Toast.makeText(this@PaytmActivity, "Payment Successful!", Toast.LENGTH_LONG).show()
//                        // Apne app ke success flow me jao
//                        finish()
                    } else if (it.contains("failure")) {

                        val resultIntent = Intent().apply {
                            putExtra("result_key", "Failure")
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()

//                        Toast.makeText(this@PaytmActivity, "Payment Failed!", Toast.LENGTH_LONG).show()
//                        // Apne failure handling logic
//                        finish()
                    }
                }
            }
        }
    }

    private fun buildPostData(params: Map<String, String>): String {
        return params.entries.joinToString("&") { "${it.key}=${Uri.encode(it.value)}" }
    }



    private fun callPaymentTaskPayU() {
        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("1.00")
            .setIsProduction(false) // TEST MODE
            .setKey("292KmR") // TEST KEY
            .setProductInfo("Test Product")
            .setTransactionId("TXN123456")
            .setPhone("9999999999")
            .setFirstName("John")
            .setEmail("john@example.com")
            .setSurl("http://awplconnectadmin.tgastaging.com/api/payu/payment-callback") // TEST URL
            .setFurl("http://awplconnectadmin.tgastaging.com/api/payu/payment-callback")
            .build()

        PayUCheckoutPro.open(this@PaytmActivity, payUPaymentParams, object : PayUCheckoutProListener {
            override fun onPaymentSuccess(response: Any) {
                Log.d("PayU", "✅ Payment Success: $response")
            }

            override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                Log.d("PayU", "WebView properties set")
            }

            override fun onPaymentFailure(response: Any) {
                Log.d("PayU", "❌ Payment Failed: $response")
            }

            override fun generateHash(
                valueMap: HashMap<String, String?>,
                hashGenerationListener: PayUHashGenerationListener
            ) {
                if ( valueMap.containsKey(CP_HASH_STRING)
                    && valueMap.containsKey(CP_HASH_STRING) != null
                    && valueMap.containsKey(CP_HASH_NAME)
                    && valueMap.containsKey(CP_HASH_NAME) != null) {

                    val hashData = valueMap[CP_HASH_STRING]
                    val hashName = valueMap[CP_HASH_NAME]

                    //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                    val hash: String? = "9698ab90dafc17b3a92e7037f30b02ba44f9e357ce85477b320a874f2d1a73f602e0cae04e9789b60c235cd91e6cdbe1383ca91785b0debdf43d3b120fa65c86"
                    if (!TextUtils.isEmpty(hash)) {
                        val dataMap: HashMap<String, String?> = HashMap()
                        dataMap[hashName!!] = hash!!
                        hashGenerationListener.onHashGenerated(dataMap)
                    }
                }
            }

            override fun onError(errorResponse: ErrorResponse) {
                Log.e("PayU", "🚨 ERROR: ${errorResponse.errorMessage}")
            }

            override fun onPaymentCancel(isTxnInitiated: Boolean) {
                Log.d("PayU", "🚫 Payment Cancelled | Txn started: $isTxnInitiated")
            }
        })
    }


    fun generateSHA512Hash(input: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
//    private fun paymentWork(){
//        Checkout.preload(applicationContext)
//        val co = Checkout()
//        // apart from setting it in AndroidManifest.xml, keyId can also be set
//        // programmatically during runtime
//        co.setKeyID("rzp_live_XXXXXXXXXXXXXX")
//        var amount :Int = Math.round(amt.toFloat() *100)
//        var jsonObject = JsonObject()
//       try {
//           val options = JSONObject()
//           options.put("name","AWPL PAYMENT")
//           options.put("description","Fees")
//           options.put("currency","INR")
//           options.put("theme.color","")
//           options.put("amount",amount)
//           co.open(this,options)
//       }catch (e:Exception){
//           Log.d("TAG",e.message.toString())
//       }
//    }
//
//    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
//    }
//
//
//
//    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
//    }


}