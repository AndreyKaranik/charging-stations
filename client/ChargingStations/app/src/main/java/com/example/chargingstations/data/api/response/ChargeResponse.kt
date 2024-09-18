package com.example.chargingstations.data.api.response

import com.google.gson.annotations.SerializedName

data class ChargeResponse(
    @SerializedName("order_id")
    val orderId: Int
)
