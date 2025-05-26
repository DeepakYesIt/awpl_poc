package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.PayuPaymentModel
import com.bussiness.awpl.model.PromoCodeModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {


    var date :String =""
    var time :String =""
    var appoitmentId :Int =0

    suspend fun applyPromoCode(
        promoCode: String,
        appointmentId: Int
    ) : Flow<NetworkResult<PromoCodeModel>> {
        return repository.applyPromoCode(promoCode, appointmentId).onEach {

        }
    }


    suspend fun initiatePayment(
        appointmentId: Int,
        txnId: String,
        amount: String,
        productInfo: String,
        firstName: String?,
        email: String?,
        phone: String?
    ): Flow<NetworkResult<PayuPaymentModel>>{
        return repository.initiatePayment(appointmentId, txnId, amount, productInfo, firstName, email, phone).onEach {

        }
    }

}