package com.example.currencyapp.network

import com.example.currencyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object API {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private val client by lazy {
        OkHttpClient.Builder().apply {

            addInterceptor(logging)
        }.build()
    }

    private val logging by lazy {
        HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    val currency: CurrencyAPI by lazy {
        retrofit.create(CurrencyAPI::class.java)
    }
}