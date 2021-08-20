package com.instantonlinematka.instantonlinematka.retrofit

import com.instantonlinematka.instantonlinematka.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitCheckStatus {

    // BASE URL
    var BASE_URL = "https://biz.traknpay.in/"

    private lateinit var logging: HttpLoggingInterceptor

    private lateinit var okHttpClient: OkHttpClient.Builder

    private lateinit var retrofit: Retrofit

    fun getRetrfitInstance() : ApiInterface {

        val typeOfLogging =
            if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        logging = HttpLoggingInterceptor()
        logging.setLevel(typeOfLogging)

        okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logging)
        okHttpClient.readTimeout(120, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(120, TimeUnit.SECONDS)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiInterface::class.java)

    }
}