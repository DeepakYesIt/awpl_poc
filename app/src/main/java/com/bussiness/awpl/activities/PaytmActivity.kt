package com.bussiness.awpl.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bussiness.awpl.R
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaytmActivity : AppCompatActivity(), PaymentResultWithDataListener {

    var amt :String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paytm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       paymentWork()
    }

    fun paymentWork(){
        Checkout.preload(applicationContext)
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_live_XXXXXXXXXXXXXX")
        var amount :Int = Math.round(amt.toFloat() *100)
        var jsonObject = JsonObject()
       try {
           val options = JSONObject()
           options.put("name","AWPL PAYMENT")
           options.put("description","Fees")
           options.put("currency","INR")
           options.put("theme.color","")
           options.put("amount",amount)
           co.open(this,options)
       }catch (e:Exception){
           Log.d("TAG",e.message.toString())
       }

    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

    }

    

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {

    }


}