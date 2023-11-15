package net.toughcoder.aeolus.data.qweather

import net.toughcoder.aeolus.BuildConfig
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

    @GET("v2/city/lookup")
    suspend fun searchCity(
        @Query("location") query: String,
        @Query("number") number: Int = 10,
        @Query("range") range: String = "cn",
        @Query("key") key: String = BuildConfig.QWEATHER_API_KEY
    ) : QWeatherSearchResponse

    companion object {
        const val BASE_URL = "https://geoapi.qweather.com/"
    }
}