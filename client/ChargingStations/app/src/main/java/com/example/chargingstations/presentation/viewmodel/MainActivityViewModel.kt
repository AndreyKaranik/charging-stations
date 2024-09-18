package com.example.chargingstations.presentation.viewmodel

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargingstations.data.api.Api
import com.example.chargingstations.domain.model.ChargingStationDetails
import com.example.chargingstations.domain.model.ChargingStationMedium
import com.example.chargingstations.domain.model.ChargingStationMin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class MainActivityViewModel : ViewModel() {

    private val TAG: String = "MainActivityViewModel"

    private val _accountSheetIsShown = MutableStateFlow(false)
    val accountSheetIsShown: StateFlow<Boolean> = _accountSheetIsShown

    private val _chargingStationImageBitmap = MutableStateFlow<ImageBitmap?>(null)
    val chargingStationImageBitmap: StateFlow<ImageBitmap?> = _chargingStationImageBitmap

    private val _chargingStations = MutableStateFlow<List<ChargingStationMin>>(emptyList())
    val chargingStations: StateFlow<List<ChargingStationMin>> = _chargingStations

    private val _filteredChargingStations =
        MutableStateFlow<List<ChargingStationMedium>>(emptyList())
    val filteredChargingStations: StateFlow<List<ChargingStationMedium>> = _filteredChargingStations

    private val _chargingStationsFetching = MutableStateFlow(false)
    val chargingStationsFetching: StateFlow<Boolean> = _chargingStationsFetching

    private val _chargingStationsFetched = MutableStateFlow(false)
    val chargingStationsFetched: StateFlow<Boolean> = _chargingStationsFetched

    private val _chargingStationDetailsFetched = MutableStateFlow(false)
    val chargingStationDetailsFetched: StateFlow<Boolean> = _chargingStationDetailsFetched

    private val _chargingStationDetailsFetching = MutableStateFlow(false)
    val chargingStationDetailsFetching: StateFlow<Boolean> = _chargingStationDetailsFetching

    private val _cSDetailsFetchProblemIsShown = MutableStateFlow(false)
    val cSDetailsFetchProblemIsShown: StateFlow<Boolean> = _cSDetailsFetchProblemIsShown

    private val _filteredChargingStationsFetching = MutableStateFlow(false)
    val filteredChargingStationsFetching: StateFlow<Boolean> = _filteredChargingStationsFetching

    private val _filteredChargingStationsFetched = MutableStateFlow(false)
    val filteredChargingStationsFetched: StateFlow<Boolean> = _filteredChargingStationsFetched

    private val _searchProblemIsShown = MutableStateFlow(false)
    val searchProblemIsShown: StateFlow<Boolean> = _searchProblemIsShown

    private val _noInternetConnectionDialogIsShown = MutableStateFlow(false)
    val noInternetConnectionDialogIsShown: StateFlow<Boolean> = _noInternetConnectionDialogIsShown

    private val _chargingStationDetailsSheetIsShown = MutableStateFlow(false)
    val chargingStationDetailsSheetIsShown: StateFlow<Boolean> = _chargingStationDetailsSheetIsShown

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _gpsProgressIndicatorIsShown = MutableStateFlow(false)
    val gpsProgressIndicatorIsShown: StateFlow<Boolean> = _gpsProgressIndicatorIsShown

    private val _gpsDialogIsShown = MutableStateFlow(false)
    val gpsDialogIsShown: StateFlow<Boolean> = _gpsDialogIsShown

    private val _incorrectQRCodeDialogIsShown = MutableStateFlow(false)
    val incorrectQRCodeDialogIsShown: StateFlow<Boolean> = _incorrectQRCodeDialogIsShown

    private val _chargingStationNotFoundDialogIsShown = MutableStateFlow(false)
    val chargingStationNotFoundDialogIsShown: StateFlow<Boolean> =
        _chargingStationNotFoundDialogIsShown

    private val _connectionProblemDialogIsShown = MutableStateFlow(false)
    val connectionProblemDialogIsShown: StateFlow<Boolean> = _connectionProblemDialogIsShown

    private val _chargingStationDetails = MutableStateFlow<ChargingStationDetails?>(null)
    val chargingStationDetails: StateFlow<ChargingStationDetails?> = _chargingStationDetails


    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(logging)
    }.build()

    private var lastChargingStationDetailsId: Int? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://194.67.88.154:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    private val apiService = retrofit.create(Api::class.java)

    init {
        fetchChargingStations()
    }

    fun setToken(token: String?) {
        _token.value = token
    }

    fun setEmail(email: String?) {
        _email.value = email
    }

    fun showGPSDialog() {
        _gpsDialogIsShown.value = true
    }

    fun hideGPSDialog() {
        _gpsDialogIsShown.value = false
    }

    fun showNoInternetConnectionDialog() {
        _noInternetConnectionDialogIsShown.value = true
    }

    fun hideNoInternetConnectionDialog() {
        _noInternetConnectionDialogIsShown.value = false
    }

    fun showIncorrectQRCodeDialogIsShown() {
        _incorrectQRCodeDialogIsShown.value = true
    }

    fun hideIncorrectQRCodeDialogIsShown() {
        _incorrectQRCodeDialogIsShown.value = false
    }

    fun showChargingStationNotFoundDialogIsShown() {
        _chargingStationNotFoundDialogIsShown.value = true
    }

    fun hideChargingStationNotFoundDialogIsShown() {
        _chargingStationNotFoundDialogIsShown.value = false
    }

    fun showConnectionProblemDialog() {
        _connectionProblemDialogIsShown.value = true
    }

    fun hideConnectionProblemDialog() {
        _connectionProblemDialogIsShown.value = false
    }


    fun showGPSProgressIndicator() {
        _gpsProgressIndicatorIsShown.value = true
    }

    fun hideGPSProgressIndicator() {
        _gpsProgressIndicatorIsShown.value = false
    }

    fun showAccountSheet() {
        _accountSheetIsShown.value = true
    }

    fun hideAccountSheet() {
        _accountSheetIsShown.value = false
    }
    fun showChargingStationDetailsSheet(chargingStationId: Int) {
        _chargingStationDetailsSheetIsShown.value = true
        lastChargingStationDetailsId = chargingStationId
        _cSDetailsFetchProblemIsShown.value = false
        fetchChargingStationDetails(chargingStationId)
    }

    fun fetchChargingStationDetails(chargingStationId: Int) {
        _chargingStationDetailsFetching.value = true
        viewModelScope.launch {
            try {
                val response =
                    apiService.getChargingStationDetails(chargingStationId).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let {
                        it.chargingMarksWithUserName.forEach { mark ->
                            mark.time = mark.time.replace(Regex("[.].*"), "")
                        }
                        _chargingStationDetails.value = it
                        _chargingStationImageBitmap.value = null
                        _chargingStationDetailsFetched.value = true
                        if (it.imageIds.isNotEmpty()) {
                            fetchChargingStationImage(it.imageIds[0])
                        }
                    }
                } else {
                    _cSDetailsFetchProblemIsShown.value = true
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
                _cSDetailsFetchProblemIsShown.value = true
                e.printStackTrace()
                Log.e(TAG, "exception")
            } finally {
                _chargingStationDetailsFetching.value = false
            }
        }
    }

    fun hideChargingStationDetailsSheet() {
        _chargingStationDetailsSheetIsShown.value = false
        _chargingStationDetails.value = null
    }

    fun getChargingStationById(chargingStationId: Int): ChargingStationMin? {
        return chargingStations.value.find {
            it.id == chargingStationId
        }
    }

    fun fetchChargingStations() {
        _chargingStationsFetching.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getChargingStations("min", null).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let {
                        val chargingStationList: List<ChargingStationMin> = it.map { station ->
                            ChargingStationMin(
                                station.id!!,
                                station.latitude!!,
                                station.longitude!!
                            )
                        }
                        _chargingStations.value = chargingStationList
                        _chargingStationsFetched.value = true
                    }
                } else {
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "exception")
            } finally {
                _chargingStationsFetching.value = false
            }
        }
    }

    fun fetchFilteredChargingStations() {
        _filteredChargingStationsFetching.value = true
        viewModelScope.launch {
            try {
                val response =
                    apiService.getChargingStations("medium", searchQuery.value).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let {
                        val chargingStationList: List<ChargingStationMedium> = it.map { station ->
                            ChargingStationMedium(
                                station.id!!,
                                station.name!!,
                                station.address!!,
                                station.latitude!!,
                                station.longitude!!,
                                station.chargingTypes!!
                            )
                        }
                        _filteredChargingStations.value = chargingStationList
                        _filteredChargingStationsFetched.value = true
                    }
                } else {
                    _searchProblemIsShown.value = true
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
                _searchProblemIsShown.value = true
                e.printStackTrace()
                Log.e(TAG, "exception")
            } finally {
                _filteredChargingStationsFetching.value = false
            }
        }
    }

    fun fetchChargingStationImage(chargingStationImageId: Int) {
//        _filteredChargingStationsFetching.value = true
        viewModelScope.launch {
            try {
                val response =
                    apiService.getChargingStationImage(chargingStationImageId).awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let {
                        val decodedString = Base64.decode(it.data, Base64.DEFAULT)
                        val bitmap =
                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        _chargingStationImageBitmap.value = bitmap.asImageBitmap()
//                        _filteredChargingStationsFetched.value = true
                    }
                } else {
//                    _searchProblemIsShown.value = true
                    Log.e(TAG, "error")
                }
            } catch (e: Exception) {
//                _searchProblemIsShown.value = true
                e.printStackTrace()
                Log.e(TAG, "exception")
            } finally {
//                _filteredChargingStationsFetching.value = false
            }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery != "") {
            _filteredChargingStationsFetched.value = false
            fetchFilteredChargingStations()
        } else {
            _filteredChargingStations.value = emptyList()
        }
    }

    fun tryAgainSearch() {
        _searchProblemIsShown.value = false;
        _filteredChargingStationsFetched.value = false
        fetchFilteredChargingStations()
    }

    fun tryAgainFetchCSDetails() {
        _cSDetailsFetchProblemIsShown.value = false
        _chargingStationDetailsFetched.value = false
        lastChargingStationDetailsId?.let {
            fetchChargingStationDetails(it)
        }
    }
}