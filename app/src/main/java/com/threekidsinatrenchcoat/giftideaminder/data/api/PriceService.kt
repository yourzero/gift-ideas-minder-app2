package com.threekidsinatrenchcoat.giftideaminder.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PriceService {
    @GET("product")
    suspend fun getPriceHistory(@Query("ids") asin: String, @Query("key") apiKey: String): PriceResponse

    data class PriceResponse(
        val current_price: Double,
        val price_history: List<PricePoint>
    )

    data class PricePoint(
        val date: String,
        val price: Double
    )
}

object PriceApi {
    private const val BASE_URL = "https://api.camelcamelcamel.com/v1/"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PriceService = retrofit.create(PriceService::class.java)
} 