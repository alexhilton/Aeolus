package net.toughcoder.aeolus.data.qweather

import net.toughcoder.aeolus.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

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

    @GET("v7/indices/1d")
    suspend fun fetchWeatherIndices(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("lang") lang: String = "zh",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherIndexResponse

    @GET("v7/indices/3d")
    suspend fun fetch3DWeatherIndices(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("lang") lang: String = "zh",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherIndexResponse

    companion object {
        const val BASE_URL = "https://devapi.qweather.com/"
        const val CONN_TIMEOUT = 60L
        const val TIMEOUT = 30L

        inline fun <reified T> create(baseUrl: String): T {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC}

            val client = OkHttpClient.Builder()
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .callTimeout(TIMEOUT, TimeUnit.SECONDS)
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