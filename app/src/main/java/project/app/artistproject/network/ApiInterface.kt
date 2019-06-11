package com.zeniex.www.zeniexautomarketing.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.logging.HttpLoggingInterceptor
import project.app.artistproject.BuildConfig
import java.util.concurrent.TimeUnit

interface ApiInterface {

    companion object {
        fun create(): ApiInterface {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor { chain -> chain.proceed(chain.request()) }
                    .addInterceptor(interceptor)
                    .build()

            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.JSON_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }


    @GET("uamp/catalog.json")
    fun getJson() : Single<JsonObject>

}