package com.example.chargingstations.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargingstations.data.api.Api
import com.example.chargingstations.data.api.request.ChargeRequest
import com.example.chargingstations.data.api.request.MarkRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class OrderActivityViewModel : ViewModel() {

    private val TAG: String = "OrderActivityViewModel"

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(logging)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://194.67.88.154:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    private val apiService = retrofit.create(Api::class.java)

    private val _connectorId = MutableStateFlow<Int?>(null)
    val connectorId: StateFlow<Int?> = _connectorId

    private val _chargingStationId = MutableStateFlow<Int?>(null)
    val chargingStationId: StateFlow<Int?> = _chargingStationId

    private val _chargingTypeId = MutableStateFlow<Int?>(null)
    val chargingTypeId: StateFlow<Int?> = _chargingTypeId

    private val _chargingStationAddress = MutableStateFlow<String?>(null)
    val chargingStationAddress: StateFlow<String?> = _chargingStationAddress

    private val _connectorTypeName = MutableStateFlow<String?>(null)
    val connectorTypeName: StateFlow<String?> = _connectorTypeName

    private val _connectorRate = MutableStateFlow<Float?>(null)
    val connectorRate: StateFlow<Float?> = _connectorRate

    private val _amount = MutableStateFlow<String>("10.0")
    val amount: StateFlow<String> = _amount

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _orderId = MutableStateFlow<Int?>(null)
    val orderId: StateFlow<Int?> = _orderId

    private val _orderStatus = MutableStateFlow<Int?>(null)
    val orderStatus: StateFlow<Int?> = _orderStatus

    private val _orderProgress = MutableStateFlow<Int?>(null)
    val orderProgress: StateFlow<Int?> = _orderProgress

    private val _finished = MutableStateFlow<Boolean>(false)
    val finished: StateFlow<Boolean> = _finished

    private val _amountIsIncorrect = MutableStateFlow<Boolean>(false)
    val amountIsIncorrect: StateFlow<Boolean> = _amountIsIncorrect

    fun setConnectorId(connectorId: Int?) {
        _connectorId.value = connectorId
    }

    fun setChargingStationId(chargingStationId: Int?) {
        _chargingStationId.value = chargingStationId
    }

    fun setChargingTypeId(chargingTypeId: Int?) {
        _chargingTypeId.value = chargingTypeId
    }

    fun setChargingStationAddress(chargingStationAddress: String?) {
        _chargingStationAddress.value = chargingStationAddress
    }

    fun setConnectorTypeName(typeName: String?) {
        _connectorTypeName.value = typeName
    }

    fun setConnectorRate(connectorRate: Float?) {
        _connectorRate.value = connectorRate
    }

    fun setAmount(amount: String) {
        _amount.value = amount
        _amountIsIncorrect.value = true
        val regex = Regex("^[0-9]*\\.?[0-9]+$")
        if (regex.matches(amount)) {
            try {
                val a = amount.toDouble()
                if (a > 0) {
                    _amountIsIncorrect.value = false
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
    }

    fun setToken(token: String?) {
        _token.value = token
    }

    fun charge() {
        viewModelScope.launch {
            try {
                val a = amount.value.toFloat()
                val response = apiService.charge(
                    ChargeRequest(connectorId = connectorId.value!!, amount = a, token = token.value)
                ).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _orderId.value = it.orderId
                        while (_orderStatus.value != 1) {
                            val getOrderResponse = apiService.getOrder(orderId = it.orderId).awaitResponse()
                            if (getOrderResponse.isSuccessful) {
                                getOrderResponse.body()?.let { r ->
                                    _orderStatus.value = r.status
                                    _orderProgress.value = r.progress
                                }
                            } else {
                                Log.e(TAG, "error")
                            }
                            delay(100)
                        }
                    }
                } else {
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "exception")
            }
        }
    }

    fun mark(status: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.mark(
                    MarkRequest(
                        chargingStationId = chargingStationId.value!!,
                        status = status,
                        chargingTypeId = chargingTypeId.value!!,
                        token = token.value
                    )
                ).awaitResponse()
                if (response.isSuccessful) {
                    _finished.value = true
                } else {
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "exception")
            }
        }
    }
}