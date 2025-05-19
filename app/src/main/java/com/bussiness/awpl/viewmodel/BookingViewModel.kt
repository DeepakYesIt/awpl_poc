package com.bussiness.awpl.viewmodel

import androidx.lifecycle.ViewModel
import com.bussiness.awpl.NetworkResult
import com.bussiness.awpl.model.BookingResponseModel
import com.bussiness.awpl.model.PromoCodeModel
import com.bussiness.awpl.repository.AwplRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private var repository: AwplRepository): ViewModel() {


    var selectedCallId =0

    var selectedDateStr =""
    var selectTime =""
    suspend fun booking(
        date: String, time :String, callId :Int
    ) : Flow<NetworkResult<BookingResponseModel>> {
        return repository.bookingAppointment(  date, time, callId).onEach {

        }
    }


    suspend fun getScheduleTime(date :String): Flow<NetworkResult<MutableList<String>>>{
        return repository.getScheduleTime(date).onEach {

        }
    }

     suspend fun applyPromoCode(
        promoCode: String,
        appointmentId: Int
    ) : Flow<NetworkResult<PromoCodeModel>>{
        return repository.applyPromoCode(promoCode, appointmentId).onEach {

        }
    }

}