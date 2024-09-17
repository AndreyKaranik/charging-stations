package com.example.chargingstations.domain.model.response

import com.google.gson.annotations.SerializedName

data class GetOrderResponse(
    @SerializedName("connector_id")
    val connectorId: Int,
    @SerializedName("user_Id")
    val userId: Int?,
    val amount: Float,
    val status: Int,
    val progress: Int
)