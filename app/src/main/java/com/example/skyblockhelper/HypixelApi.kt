package com.example.skyblockhelper

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface HypixelApi {

    @GET("player")
    suspend fun getPlayer(
        @Query("key") apiKey: String,
        @Query("name") playerName: String
    ): PlayerResponse

    @GET("resources/skyblock/election")
    suspend fun getElection(
        @Query("key") apiKey: String
    ): ElectionResponse

    @GET("skyblock/bazaar")
    suspend fun getBazaar(@Query("key") apiKey: String): BazaarResponse

    companion object {
        const val API_KEY = "f022251d-36a4-461f-a5f9-9bb193704d20"

        fun create(): HypixelApi {
            return Retrofit.Builder()
                .baseUrl("https://api.hypixel.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HypixelApi::class.java)
        }
    }
}