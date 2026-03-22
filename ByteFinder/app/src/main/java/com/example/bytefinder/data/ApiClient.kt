package com.example.bytefinder.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Emulador Android
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Telemóvel real na mesma rede Wi-Fi do PC
    // private const val BASE_URL = "http://192.168.1.100:8000/"

    // Produção / backend publicado
    // private const val BASE_URL = "https://teu-backend.azurewebsites.net/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}