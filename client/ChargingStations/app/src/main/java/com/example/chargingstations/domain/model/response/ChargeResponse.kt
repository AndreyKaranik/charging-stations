package com.example.chargingstations.domain.model.response

import com.google.gson.annotations.SerializedName

data class ChargeResponse(
    @SerializedName("order_id")
    val orderId: Int
)
