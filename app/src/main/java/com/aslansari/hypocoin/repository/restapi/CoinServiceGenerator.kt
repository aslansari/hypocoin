package com.aslansari.hypocoin.repository.restapi

import com.aslansari.hypocoin.BuildConfig
import com.aslansari.hypocoin.app.HypoCoinApp.Companion.instance
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object CoinServiceGenerator {
    private const val DISK_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    private val gson = GsonBuilder().create()
    private val retrofitBuilder = Retrofit.Builder()
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
    private val defaultOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .build()

    fun <S> createService(serviceClass: Class<S>?, baseUrl: String?): S {
        return createService(serviceClass, baseUrl, null, null)
    }

    fun <S> createService(
        serviceClass: Class<S>?,
        baseUrl: String?,
        networkInterceptor: Interceptor?,
        cacheInterceptor: Interceptor?
    ): S {
        val okHttpClientBuilder: OkHttpClient.Builder = defaultOkHttpClient.newBuilder()
        if (networkInterceptor != null) {
            okHttpClientBuilder.addNetworkInterceptor(networkInterceptor)
        }
        if (cacheInterceptor != null) {
            okHttpClientBuilder.addNetworkInterceptor(cacheInterceptor)
        }
        val modifiedOkHttpClient: OkHttpClient = okHttpClientBuilder
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        retrofitBuilder.client(modifiedOkHttpClient)
        retrofitBuilder.baseUrl(baseUrl ?: "")
        val retrofit = retrofitBuilder.build()
        return retrofit.create(serviceClass)
    }

    // Install an HTTP cache in the application cache directory.
    private val cache: Cache?
        private get() {
            var cache: Cache? = null
            // Install an HTTP cache in the application cache directory.
            try {
                val cacheDir = File(instance!!.cacheDir, "apiResponses")
                cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cache
        }
    private val httpLoggingInterceptor: HttpLoggingInterceptor
        private get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            return httpLoggingInterceptor
        }
}