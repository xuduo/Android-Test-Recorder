package com.xd.mvvm.boilerplate.sharedpref

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    @Named("config")
    fun provideSharedPreferencesHelperConfig(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, "config", moshi)
    }

    @Provides
    @Singleton
    @Named("cache")
    fun provideSharedPreferencesHelperCache(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, "cache", moshi)
    }

    @Provides
    @Singleton
    @Named("config_simulate_http_error")
    fun provideHttpError(
        @Named("config") sharedPreferencesHelper: SharedPreferencesHelper
    ): BooleanSharedPreferenceLiveData {
        return sharedPreferencesHelper.getBooleanLiveData("simulate_http_error")
    }

    @Provides
    @Singleton
    @Named("config_simulate_http_latency")
    fun provideHttpLatency(
        @Named("config") sharedPreferencesHelper: SharedPreferencesHelper
    ): BooleanSharedPreferenceLiveData {
        return sharedPreferencesHelper.getBooleanLiveData("simulate_http_latency")
    }
}
