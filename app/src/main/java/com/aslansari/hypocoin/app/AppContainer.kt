package com.aslansari.hypocoin.app

import android.content.Context
import com.aslansari.hypocoin.BuildConfig
import com.aslansari.hypocoin.register.Register
import com.aslansari.hypocoin.viewmodel.CoinViewModel
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import com.aslansari.hypocoin.register.RegisterViewModel
import com.aslansari.hypocoin.repository.CoinDatabase
import com.aslansari.hypocoin.repository.restapi.CoinAPI
import com.aslansari.hypocoin.repository.CoinRepository
import com.aslansari.hypocoin.repository.AccountRepository
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    val coinDatabase: CoinDatabase
    val coinAPI: CoinAPI

    private val DISK_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    private val gson = GsonBuilder().create()
    private val retrofitBuilder = Retrofit.Builder()
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
    private val defaultOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .build()

    init {
        val okHttpClientBuilder: OkHttpClient.Builder = defaultOkHttpClient.newBuilder()
        val modifiedOkHttpClient: OkHttpClient = okHttpClientBuilder
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        retrofitBuilder.client(modifiedOkHttpClient)
        retrofitBuilder.baseUrl(CoinAPI.BASE_URL)
        val retrofit = retrofitBuilder.apply {
            client(modifiedOkHttpClient)
            baseUrl(CoinAPI.BASE_URL)
        }.build()

        coinDatabase = CoinDatabase.getDatabase(context)
        coinAPI = retrofit.create(CoinAPI::class.java)
    }

    // Install an HTTP cache in the application cache directory.
    private val cache: Cache?
        get() {
            var cache: Cache? = null
            // Install an HTTP cache in the application cache directory.
            try {
                val cacheDir = File(HypoCoinApp.instance!!.cacheDir, "apiResponses")
                cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cache
        }
    private val httpLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            return httpLoggingInterceptor
        }
}