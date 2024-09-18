package com.example.chargingstations.data.api.request

import com.google.gson.annotations.SerializedName

data class MarkRequest(
    @SerializedName("charging_station_id")
    val chargingStationId: Int,
    val status: Int,
    @SerializedName("charging_type_id")
    val chargingTypeId: Int,
    val token: String?
)