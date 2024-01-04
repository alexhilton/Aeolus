package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.NO_ERROR

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.WeatherUiState,
    gotoDaily: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        NowWeatherSection(modifier, uiState)

        HourlyWeatherSection(modifier, uiState.hourlyStates)

        DailyWeatherSection(modifier, dailyWeathers = uiState.dailyStates, gotoDaily)

        WeatherIndexSection(modifier, uiState.weatherIndices)
    }
}

@Preview
@Composable
fun DailyInfoPreview() {
    GeneralInfo(aqi = "30", info = DailyUiState(
        "2023-11-29",
        "19 \u2103",
        19f,
        "1 \u2103",
        1f,
        "05:00",
        "18:00",
        net.toughcoder.qweather.R.drawable.ic_101,
        "Good",
        "1"
    ))
}

@Preview
@Composable
fun DetailPreview() {
    val state = HomeUiState.WeatherUiState(
        temp = "25 \u2103",
        feelsLike = "28 \u2103",
        icon = net.toughcoder.qweather.R.drawable.ic_101,
        text = "Cloudy",
        windDegree = 128f,
        iconDir = R.drawable.ic_nav,
        windDir = "SW",
        windScale = "12 级",
        windSpeed = "3 km/h",
        humidity = "78 %",
        pressure = "1024 pa",
        visibility = "124 km",
        aqi = "20",
        city = CityState("Nanjing", "123", "Jiang Su"),
        isLoading = false,
        dailyStates = listOf(
            DailyUiState(
                "2023-11-18",
                "29 \u2103",
                29f,
                "1 \u2103",
                1f,
                "05:00",
                "18:00",
                iconDay = net.toughcoder.qweather.R.drawable.ic_1001,
                textDay = "Good",
                uvIndex = "1"
            )
        ),
        hourlyStates = emptyList(),
        weatherIndices = emptyList(),
        errorMessage = NO_ERROR
    )
    NowWeatherSection(
        Modifier.fillMaxWidth(),
        uiState = state
    )
}