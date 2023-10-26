package net.toughcoder.aeolus.data.weather.api

import net.toughcoder.aeolus.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface QWeatherService {
    @GET("v7/weather/now")
    suspend fun fetchWeatherNow(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("unit") unit: String = "m",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherNowResponse

    companion object {
        private const val BASE_URL = "https://devapi.qweather.com/"

        fun create(): QWeatherService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC}

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QWeatherService::class.java)
        }
    }
}