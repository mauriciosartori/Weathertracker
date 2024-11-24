package com.nooro.weathertracker.hilt

import com.nooro.weathertracker.BuildConfig
import com.nooro.weathertracker.network.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging) // Add logging for debugging
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url
                val urlWithApiKey = originalUrl.newBuilder()
                    .addQueryParameter("key", BuildConfig.WEATHER_API_KEY)
                    .build()
                val requestWithApiKey = original.newBuilder()
                    .url(urlWithApiKey)
                    .build()
                chain.proceed(requestWithApiKey)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }
}