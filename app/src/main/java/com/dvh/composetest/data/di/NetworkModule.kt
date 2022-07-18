package com.dvh.composetest.data.di

import com.dvh.composetest.BuildConfig
import com.dvh.composetest.data.network.Api
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private val gson: Gson by lazy { provideGson() }
    private val okHttpClient: OkHttpClient by lazy { provideOkHttpClient() }
    private val retrofit: Retrofit by lazy { provideRetrofit(gson, okHttpClient) }

    val api: Api = retrofit.create(Api::class.java)


    private fun provideGson(): Gson {
        return Gson().newBuilder()
            .setLenient()
            .setDateFormat("sda")
            .serializeNulls()
            .create()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()

        httpBuilder.addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )

        httpBuilder.callTimeout(60, TimeUnit.SECONDS)
        httpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        httpBuilder.readTimeout(60, TimeUnit.SECONDS)
        httpBuilder.writeTimeout(60, TimeUnit.SECONDS)

        return httpBuilder.build()
    }

    private fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}