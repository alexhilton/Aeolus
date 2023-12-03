package net.toughcoder.aeolus.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun BigLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun ItemDescription(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleLabel(title)
        ValueLabel(value)
    }
}

@Composable
fun TitleLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .paddingFromBaseline(top = 24.dp, bottom = 8.dp),
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        style=  MaterialTheme.typography.titleMedium
    )
}

@Composable
fun ValueLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .paddingFromBaseline(top = 24.dp, bottom = 8.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.End
    )
}

@Composable
fun GeneralText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun IconTitleInfo(
    modifier: Modifier = Modifier,
    @StringRes text: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun WeatherSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        content()
    }
}

@Preview
@Composable
fun DailyInfoPreview() {
    GeneralInfo(aqi = "30", info = DailyUiState(
        "2023-11-29",
        "19 \u2103",
        "1 \u2103",
        "05:00",
        "18:00",
        R.drawable.ic_101,
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
        icon = R.drawable.ic_101,
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
                "1 \u2103",
                "05:00",
                "18:00",
                iconDay = R.drawable.ic_1001,
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