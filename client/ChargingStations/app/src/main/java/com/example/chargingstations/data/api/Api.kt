package com.example.chargingstations.data.api
import com.example.chargingstations.data.api.request.AuthRequest
import com.example.chargingstations.data.api.response.RegisterResponse
import com.example.chargingstations.data.api.request.RegisterRequest
import com.example.chargingstations.data.api.response.AuthResponse
import com.example.chargingstations.data.api.request.ChargeRequest
import com.example.chargingstations.data.api.request.MarkRequest
import com.example.chargingstations.data.api.response.ChargeResponse
import com.example.chargingstations.data.api.response.GetChargingStationDetailsResponse
import com.example.chargingstations.data.api.response.GetChargingStationImageResponse
import com.example.chargingstations.data.api.response.GetChargingStationsResponse
import com.example.chargingstations.data.api.response.GetOrderResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("charging-stations")
    fun getChargingStations(
        @Query("level") level: String?,
        @Query("query") query: String?
    ): Call<GetChargingStationsResponse>

    @GET("charging-stations/{id}")
    fun getChargingStationDetails(@Path("id") chargingStationId: Int): Call<GetChargingStationDetailsResponse>

    @GET("charging-station-images/{id}")
    fun getChargingStationImage(@Path("id") chargingStationImageId: Int): Call<GetChargingStationImageResponse>

    @POST("register")
    fun register(@Body body: RegisterRequest): Call<RegisterResponse>

    @POST("auth")
    fun auth(@Body body: AuthRequest): Call<AuthResponse>

    @POST("charge")
    fun charge(@Body body: ChargeRequest): Call<ChargeResponse>

    @POST("mark")
    fun mark(@Body body: MarkRequest): Call<Void>

    @GET("orders/{id}")
    fun getOrder(@Path("id") orderId: Int): Call<GetOrderResponse>
}


