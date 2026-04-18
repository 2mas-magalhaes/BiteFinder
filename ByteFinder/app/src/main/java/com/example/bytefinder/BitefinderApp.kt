package com.example.bytefinder

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import okhttp3.OkHttpClient

class BitefinderApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "BiteFinder/1.0 (Android; Coil)")
                    .build()
                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(client)
            .crossfade(true)
            .build()
    }
}
