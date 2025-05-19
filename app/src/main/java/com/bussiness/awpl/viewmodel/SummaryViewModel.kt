package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.PromoCodeModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {

    suspend fun applyPromoCode(
        promoCode: String,
        appointmentId: Int
    ) : Flow<NetworkResult<PromoCodeModel>> {
        return repository.applyPromoCode(promoCode, appointmentId).onEach {

        }
    }

}