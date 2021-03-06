package com.aslansari.hypocoin.repository.restapi

import com.aslansari.hypocoin.repository.model.Currency
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinAPI {
    @GET("/api/v2/assets")
    fun getCurrencyAssets(
        @Query("limit") limit: Int,
        @Query("fields") fields: String?,
    ): Observable<Asset<Currency>>

    @GET("/api/v2/assets")
    suspend fun getCurrencies(
        @Query("limit") limit: Int,
        @Query("fields") fields: String?,
    ): Asset<Currency>

    @GET("/api/v1/assets/{id}/metrics?fields=roi_data/percent_change_last_1_week")
    suspend fun getRoiByCurrencyId(
        @Path("id") id: String,
    ): RoiDataResult

    companion object {
        const val BASE_URL = "https://data.messari.io/"
    }
}