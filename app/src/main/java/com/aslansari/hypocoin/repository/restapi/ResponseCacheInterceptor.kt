package com.aslansari.hypocoin.repository.restapi

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor to cache data and maintain it for a minute.
 *
 *
 * If the same network request is sent within a minute,
 * the response is retrieved from cache.
 */
class ResponseCacheInterceptor(private val cacheSpan: Long) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .header("Cache-Control", "public, max-age=$cacheSpan")
            .build()
    }
}