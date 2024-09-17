package com.example.chargingstations.domain.model

import com.google.gson.annotations.SerializedName

data class ChargingStationDetails (
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("opening_hours")
    val openingHours: String,
    val description: String?,
    val connectors: List<ConnectorDetails>,
    @SerializedName("charging_marks")
    val chargingMarksWithUserName: List<ChargingMarkWithUserName>,
    @SerializedName("image_ids")
    val imageIds: List<Int>
)