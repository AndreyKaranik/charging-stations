package com.example.chargingstations.data.api.request

import com.google.gson.annotations.SerializedName

data class ChargeRequest(
    @SerializedName("connector_id")
    val connectorId: Int,
    val amount: Float,
    val token: String?
)
