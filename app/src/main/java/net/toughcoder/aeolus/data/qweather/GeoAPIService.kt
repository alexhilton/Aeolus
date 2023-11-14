package net.toughcoder.aeolus.data.qweather

import net.toughcoder.aeolus.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoAPIService {
    @GET("v2/city/top")
    suspend fun fetchTopCities(
        @Query("range") range: String = "cn",
        @Query("number") number: Int = 10,
        @Query("lang") lang: String = "zh",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherTopCitiesResponse

    companion object {
        const val BASE_URL = "https://geoapi.qweather.com/"

        fun create(): GeoAPIService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC}

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeoAPIService::class.java)
        }
    }
}