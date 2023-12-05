package com.xd.mvvm.testrecorder.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xd.mvvm.testrecorder.data.LocalDateTimeAdapter
import com.xd.mvvm.testrecorder.sharedpref.BooleanSharedPreferenceLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).add(LocalDateTimeAdapter()).build()
    }

    @Provides
    @Named("weather")
    fun provideWeatherHttp(
        client: OkHttpClient,
        moshi: Moshi,
        @Named("config_simulate_http_error")
        simulateNetworkError: BooleanSharedPreferenceLiveData,
        @Named("config_simulate_http_latency")
        simulateNetworkLatency: BooleanSharedPreferenceLiveData
    ): HttpService {
        return HttpService(
            client,
            moshi,
            "api.open-meteo.com",
            "v1/",
            simulateNetworkError,
            simulateNetworkLatency
        )
    }
}