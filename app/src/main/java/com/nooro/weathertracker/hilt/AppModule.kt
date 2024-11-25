package com.nooro.weathertracker.hilt

import android.app.Application
import android.content.Context
import com.nooro.weathertracker.WeatherTrackerApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun getApplication(application: Application): Application {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext
}