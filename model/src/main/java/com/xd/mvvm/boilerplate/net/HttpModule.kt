package com.xd.mvvm.boilerplate.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xd.mvvm.boilerplate.data.LocalDateTimeAdapter
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
        moshi: Moshi
    ): HttpService {
        return HttpService(client, moshi, "api.open-meteo.com", "v1/")
    }
}