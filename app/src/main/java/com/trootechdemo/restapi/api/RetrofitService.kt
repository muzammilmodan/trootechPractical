package com.trootechdemo.restapi.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trootechdemo.BuildConfig
import com.trootechdemo.restapi.Apis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
open class RetrofitService {
    private var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun provideHTTPClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        val httpClientBuilder = OkHttpClient().newBuilder().addInterceptor(interceptor)
        httpClientBuilder.readTimeout(120, TimeUnit.SECONDS)
        httpClientBuilder.connectTimeout(120, TimeUnit.SECONDS)
        httpClientBuilder.writeTimeout(120, TimeUnit.SECONDS)


        return httpClientBuilder
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                chain.proceed(newRequest.build())
            }.build()
    }

    @Provides
    fun apiServices(okHttpClient: OkHttpClient): ApiServices = Retrofit.Builder()
        .baseUrl(Apis.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiServices::class.java)
}


