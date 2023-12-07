package com.xd.testrecorder.sharedpref

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xd.common.data.LocalDateTimeAdapter
import com.xd.common.sharedpref.SharedPreferencesHelper
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
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).add(LocalDateTimeAdapter()).build()
    }

}
