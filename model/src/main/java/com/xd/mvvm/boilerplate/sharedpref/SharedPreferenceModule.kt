package com.xd.mvvm.boilerplate.sharedpref

import android.content.Context
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
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, "config")
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
