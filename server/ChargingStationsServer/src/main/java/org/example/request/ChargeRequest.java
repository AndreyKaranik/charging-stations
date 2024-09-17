package org.example.request;

import com.google.gson.annotations.SerializedName;

public class ChargeRequest {
    @SerializedName("connector_id")
    private int connectorId;
    private float amount;
    private String token;

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
