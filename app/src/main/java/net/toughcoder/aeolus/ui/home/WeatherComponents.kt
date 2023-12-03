package net.toughcoder.aeolus.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
        WeatherDetails(modifier, uiState)

        HourlyWeatherSection(modifier, uiState.hourlyStates)

        DailyWeatherSection(modifier, dailyWeathers = uiState.dailyStates, gotoDaily)

        WeatherIndexSection(modifier, uiState.weatherIndices)
    }
}

@Composable
fun WeatherDetails(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.WeatherUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleInfo(uiState.text, uiState.icon, uiState.temp)

            if (uiState.dailyStates.isNotEmpty() || uiState.aqi.isNotEmpty()) {
                GeneralInfo(modifier, uiState.aqi, uiState.dailyStates[0])
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WindInfo(
                    modifier = Modifier.weight(1f),
                    wind = uiState.windDir,
                    dir = uiState.windDegree,
                    iconDir = uiState.iconDir,
                    scale = uiState.windScale,
                    speed = uiState.windSpeed
                )

                OtherInfo(
                    modifier = Modifier.weight(1f),
                    feelsLike = uiState.feelsLike,
                    humidity = uiState.humidity,
                    pressure = uiState.pressure,
                    visibility = uiState.visibility
                )
            }
        }
    }
}

@Composable
fun SimpleInfo(
    weather: String,
    @DrawableRes icon: Int,
    temp: String
) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(8.dp),
            painter = painterResource(icon),
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BigLabel(weather)
            BigLabel(temp)
        }
    }
}

@Composable
fun GeneralInfo(
    modifier: Modifier = Modifier,
    aqi: String,
    info: DailyUiState
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        if (aqi.isNotEmpty()) {
            TitleLabel(stringResource(R.string.aqi_title))
            Spacer(Modifier.width(4.dp))
            ValueLabel(aqi)
            Spacer(Modifier.width(8.dp))
        }

        TitleLabel(stringResource(R.string.uv_index_title))
        Spacer(Modifier.width(4.dp))
        ValueLabel(info.uvIndex)

        Spacer(Modifier.width(8.dp))

        TitleLabel(stringResource(R.string.temp_high_title))
        Spacer(Modifier.width(4.dp))
        ValueLabel(info.tempHigh)

        Spacer(Modifier.width(8.dp))

        TitleLabel(stringResource(R.string.temp_low_title))
        Spacer(Modifier.width(4.dp))
        ValueLabel(info.tempLow)
    }
}

@Composable
fun WindInfo(
    modifier: Modifier,
    wind: String,
    dir: Float,
    @DrawableRes iconDir: Int,
    scale: String,
    speed: String
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleLabel(wind)
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .rotate(dir),
                painter = painterResource(iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
        }
        ItemDescription(stringResource(R.string.scale_title), scale)
        ItemDescription(stringResource(R.string.speed_title), speed)
    }
}

@Composable
fun OtherInfo(
    modifier: Modifier,
    feelsLike: String,
    humidity: String,
    pressure: String,
    visibility: String
) {
    Column(
        modifier = modifier
    ) {
        ItemDescription(stringResource(R.string.feelslike_title), feelsLike)
        ItemDescription(stringResource(R.string.humidity_title), humidity)
        ItemDescription(stringResource(R.string.pressure_title), pressure)
        ItemDescription(stringResource(R.string.visibility_title), visibility)
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
    WeatherDetails(
        Modifier.fillMaxWidth(),
        uiState = state
    )
}