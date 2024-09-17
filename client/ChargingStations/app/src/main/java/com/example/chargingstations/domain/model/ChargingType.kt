package com.example.chargingstations.domain.model

import com.google.gson.annotations.SerializedName

data class ChargingType (
    val id: Int,
    val name: String,
    @SerializedName("current_type")
    val currentType: String
)