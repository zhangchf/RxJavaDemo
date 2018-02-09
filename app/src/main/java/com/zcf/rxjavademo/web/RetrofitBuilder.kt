package com.zcf.rxjavademo.web

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by zhangchf on 09/02/2018.
 */

val TAG = "Retrofit Builder"

class RetrofitBuilder {

    fun build(baseUrl: String): Retrofit {

        val clientBuilder = OkHttpClient.Builder()

        clientBuilder
                .addInterceptor { chain ->
                    System.out.println("retrofit request:" + chain.request().url())
                    return@addInterceptor chain.proceed(chain.request())
                }

        clientBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)


        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build()

        return retrofit
    }
}