package org.example.request;

import com.google.gson.annotations.SerializedName;

public class MarkRequest {
    @SerializedName("charging_station_id")
    private int chargingStationId;
    private int status;
    @SerializedName("charging_type_id")
    private int chargingTypeId;
    private String token;

    public int getChargingStationId() {
        return chargingStationId;
    }

    public void setChargingStationId(int chargingStationId) {
        this.chargingStationId = chargingStationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getChargingTypeId() {
        return chargingTypeId;
    }

    public void setChargingTypeId(int chargingTypeId) {
        this.chargingTypeId = chargingTypeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
