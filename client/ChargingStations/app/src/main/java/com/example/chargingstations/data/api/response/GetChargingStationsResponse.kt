package com.example.chargingstations.data.api.response

import com.example.chargingstations.domain.model.ChargingType
import com.google.gson.annotations.SerializedName

typealias GetChargingStationsResponse = List<JsonChargingStation>

data class JsonChargingStation (
    val id: Int?,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("opening_hours")
    val openingHours: String?,
    val description: String?,
    @SerializedName("charging_types")
    val chargingTypes: List<ChargingType>?
)