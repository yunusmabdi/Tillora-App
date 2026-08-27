package com.example.Tillora.api

import android.content.Context
import com.example.Tillora.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://192.168.1.32:8000"

    private var retrofit: Retrofit? = null

    fun initialize(context: Context) {

        val tokenManager = TokenManager(context)

        val authInterceptor = Interceptor { chain ->

            val requestBuilder = chain.request().newBuilder()

            val token = tokenManager.getToken()

            if (!token.isNullOrBlank()) {

                requestBuilder.addHeader(
                    "Authorization",
                    "Bearer $token"
                )
            }

            requestBuilder.addHeader(
                "Accept",
                "application/json"
            )

            chain.proceed(
                requestBuilder.build()
            )
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val api: TilloraApi
        get() {
            return requireNotNull(retrofit) {
                "ApiClient has not been initialized. Call ApiClient.initialize(context) first."
            }.create(TilloraApi::class.java)
        }
}