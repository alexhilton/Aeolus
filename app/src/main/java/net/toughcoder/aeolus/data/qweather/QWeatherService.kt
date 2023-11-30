package net.toughcoder.aeolus.data.qweather

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


    @GET("v7/weather/3d")
    suspend fun fetchWeather3D(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("unit") unit: String = "m",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherDailyResponse


    @GET("v7/weather/7d")
    suspend fun fetchWeather7D(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("unit") unit: String = "m",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherDailyResponse

    @GET("v7/weather/24h")
    suspend fun fetchWeather24H(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("unit") unit: String = "m",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherHourlyResponse

    @GET("v7/air/now")
    suspend fun fetchAQINow(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherAirNowResponses

    @GET("v7/air/5d")
    suspend fun fetchAQIDaily(
        @Query("location") location: String,
        @Query("lang") lang: String = "zh",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherAirDailyResponse

    companion object {
        const val BASE_URL = "https://devapi.qweather.com/"

        inline fun <reified T> create(baseUrl: String): T {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC}

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(T::class.java)
        }
    }
}