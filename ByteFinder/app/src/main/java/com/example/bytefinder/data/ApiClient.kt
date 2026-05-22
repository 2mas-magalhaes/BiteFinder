package com.example.bytefinder.data

import com.example.bytefinder.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile
    private var authToken: String? = null

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val builder = chain.request()
                .newBuilder()
                .header("Accept", "application/json")

            authToken?.takeIf { it.isNotBlank() }?.let { token ->
                builder.header("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun setAuthToken(token: String?) {
        authToken = token?.takeIf { it.isNotBlank() }
    }
}
