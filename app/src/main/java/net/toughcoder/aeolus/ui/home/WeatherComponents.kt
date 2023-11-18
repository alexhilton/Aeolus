package net.toughcoder.aeolus.ui.home

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.CityState

@Composable
fun WeatherScreen(
    uiState: NowUiState.WeatherNowUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        WeatherDetails(modifier, uiState)
        Weather24Hours(modifier)
        Weather15Days(modifier)
    }
}

@Composable
fun WeatherDetails(
    modifier: Modifier = Modifier,
    uiState: NowUiState.WeatherNowUiState
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

            if (uiState.dailyStates.isNotEmpty()) {
                GeneralInfo(modifier, uiState.dailyStates[0])
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
    info: DailyUiState
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        TitleLabel("UV")
        Spacer(Modifier.width(4.dp))
        ValueLabel(info.uvIndex)

        Spacer(Modifier.width(8.dp))

        TitleLabel("最高")
        Spacer(Modifier.width(4.dp))
        ValueLabel(info.tempHigh)

        Spacer(Modifier.width(8.dp))

        TitleLabel("最低")
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
        ItemDescription("风力", scale)
        ItemDescription("风速", speed)
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
        ItemDescription("体感温度", feelsLike)
        ItemDescription("相对温度", humidity)
        ItemDescription("大气压强", pressure)
        ItemDescription("能见度", visibility)
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
fun Weather24Hours(modifier: Modifier = Modifier) {
    Text(text = "show weather in 24 hours.\n Scroll horizontally.")
}

@Composable
fun Weather15Days(modifier: Modifier = Modifier) {
    Text(text = "Show weather in 15 days. \n Scroll horizontally.")
}

@Preview
@Composable
fun DailyInfoPreview() {
    GeneralInfo(info = DailyUiState(
        "2023-11-29",
        "19 \u2103",
        "1 \u2103",
        "05:00",
        "18:00",
        "Cloudy",
        "Good",
        "1"
    ))
}

@Preview
@Composable
fun DetailPreview() {
    val state = NowUiState.WeatherNowUiState(
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
        city = CityState("Nanjing", "123", "Jiang Su"),
        isLoading = false,
        dailyStates = listOf(
            DailyUiState(
                "2023-11-18",
                "29 \u2103",
                "1 \u2103",
                "05:00",
                "18:00",
                iconDay = "Cloudy",
                textDay = "Good",
                uvIndex = "1"
            )
        ),
        errorMessage = ""
    )
    WeatherDetails(
        Modifier.fillMaxWidth(),
        uiState = state
    )
}