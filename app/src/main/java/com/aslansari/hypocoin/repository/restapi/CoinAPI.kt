package com.aslansari.hypocoin.repository.restapi

import com.aslansari.hypocoin.repository.model.Currency
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinAPI {
    @GET("/api/v2/assets")
    fun getCurrencyAssets(
        @Query("limit") limit: Int,
        @Query("fields") fields: String?,
    ): Observable<Asset<Currency>>

    companion object {
        const val BASE_URL = "https://data.messari.io/"
    }
}