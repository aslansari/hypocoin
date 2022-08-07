package com.aslansari.hypocoin.di

import android.content.Context
import com.aslansari.hypocoin.BuildConfig
import com.aslansari.hypocoin.repository.restapi.CoinAPI
import com.aslansari.hypocoin.repository.restapi.ResponseCacheInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

const val DISK_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
const val CACHE_DURATION_MINUTES = 10L

@Module
@InstallIn(SingletonComponent::class)
object CurrencyServiceModule {

    @Provides
    fun gson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @LoggingInterceptor
    fun loggingInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return httpLoggingInterceptor
    }

    @Provides
    fun retrofit(
        @ApplicationContext context: Context,
        @LoggingInterceptor loggingInterceptor: Interceptor,
        gson: Gson,
    ): Retrofit {
        // Install an HTTP cache in the application cache directory.
        var cache: Cache? = null
        // Install an HTTP cache in the application cache directory.
        try {
            val cacheDir = File(context.cacheDir, "apiResponses")
            cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val retrofitBuilder = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        val defaultOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()

        val okHttpClientBuilder: OkHttpClient.Builder = defaultOkHttpClient.newBuilder()

        val modifiedOkHttpClient: OkHttpClient = okHttpClientBuilder
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(
                ResponseCacheInterceptor(
                    TimeUnit.MINUTES.toMillis(
                        CACHE_DURATION_MINUTES
                    )
                )
            )
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        val retrofit = retrofitBuilder.apply {
            client(modifiedOkHttpClient)
            baseUrl(CoinAPI.BASE_URL)
        }.build()
        return retrofit
    }

    @Provides
    fun getApi(
        retrofit: Retrofit
    ): CoinAPI {
        return retrofit.create(CoinAPI::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor
