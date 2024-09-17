package com.example.chargingstations.domain.model.request

import com.google.gson.annotations.SerializedName

data class ChargeRequest(
    @SerializedName("connector_id")
    val connectorId: Int,
    val amount: Float,
    val token: String?
)
