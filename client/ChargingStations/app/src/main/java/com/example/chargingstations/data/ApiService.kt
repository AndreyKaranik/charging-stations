package com.example.chargingstations.data
import com.example.chargingstations.domain.model.request.AuthRequest
import com.example.chargingstations.domain.model.ChargingStationDetails
import com.example.chargingstations.domain.model.ChargingStationImage
import com.example.chargingstations.domain.model.ChargingStationJson
import com.example.chargingstations.domain.model.response.RegisterResponse
import com.example.chargingstations.domain.model.request.RegisterRequest
import com.example.chargingstations.domain.model.response.AuthResponse
import com.example.chargingstations.domain.model.request.ChargeRequest
import com.example.chargingstations.domain.model.request.MarkRequest
import com.example.chargingstations.domain.model.response.ChargeResponse
import com.example.chargingstations.domain.model.response.GetOrderResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("charging-stations")
    fun getChargingStations(
        @Query("level") level: String?,
        @Query("query") query: String?
    ): Call<List<ChargingStationJson>>

    @GET("charging-stations/{id}")
    fun getChargingStationDetails(@Path("id") chargingStationId: Int): Call<ChargingStationDetails>

    @GET("charging-station-images/{id}")
    fun getChargingStationImage(@Path("id") chargingStationImageId: Int): Call<ChargingStationImage>

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


