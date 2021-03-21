package com.aslansari.hypocoin.repository.restapi;

import com.aslansari.hypocoin.repository.model.Currency;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinAPI {

    String BASE_URL = "https://data.messari.io/";

    @GET("/api/v2/assets")
    Observable<Asset<Currency>> getCurrencyAssets(@Query("limit") int limit, @Query("fields") String fields);
}
