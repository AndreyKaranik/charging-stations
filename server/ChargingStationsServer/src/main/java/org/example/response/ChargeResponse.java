package org.example.response;

import com.google.gson.annotations.SerializedName;

public class ChargeResponse {

    @SerializedName("order_id")
    private int orderId;

    public ChargeResponse(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
