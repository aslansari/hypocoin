package com.aslansari.hypocoin.repository.restapi;

import androidx.annotation.Nullable;

import com.aslansari.hypocoin.BuildConfig;
import com.aslansari.hypocoin.app.HypoCoinApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinServiceGenerator {

    private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Gson gson = new GsonBuilder().create();
    private static final Retrofit.Builder retrofitBuilder
            = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson));
    private static final OkHttpClient defaultOkHttpClient
            = new OkHttpClient.Builder()
            .cache(getCache())
            .build();

    // No need to instantiate this class.
    private CoinServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, Interceptor networkInterceptor, @Nullable Interceptor cacheInterceptor) {
        OkHttpClient.Builder okHttpClientBuilder = defaultOkHttpClient.newBuilder();

        if (networkInterceptor != null) {
            okHttpClientBuilder.addNetworkInterceptor(networkInterceptor);
        }

        if (cacheInterceptor != null) {
            okHttpClientBuilder.addNetworkInterceptor(cacheInterceptor);
        }

        OkHttpClient modifiedOkHttpClient = okHttpClientBuilder
                .addInterceptor(getHttpLoggingInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        retrofitBuilder.client(modifiedOkHttpClient);
        retrofitBuilder.baseUrl(baseUrl);

        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    private static Cache getCache() {
        Cache cache = null;
        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(HypoCoinApp.Companion.getInstance().getCacheDir(), "apiResponses");
            cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return httpLoggingInterceptor;
    }
}
