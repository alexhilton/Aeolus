package net.toughcoder.aeolus

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeolusScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = viewModel.location,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = it.calculateTopPadding() + 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            WeatherDetails(modifier, viewModel.weatherDetail)
            Weather24Hours(modifier)
            Weather15Days(modifier)
        }
    }
}

@Composable
fun WeatherDetails(
    modifier: Modifier = Modifier,
    weatherDetail: WeatherDetail
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleInfo(weatherDetail.text, weatherDetail.temp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WindInfo(
                    modifier = Modifier.weight(1f),
                    wind = weatherDetail.windDir,
                    dir = weatherDetail.windDegree,
                    scale = weatherDetail.windScale,
                    speed = weatherDetail.windSpeed
                )

                OtherInfo(
                    modifier = Modifier.weight(1f),
                    feelsLike = weatherDetail.feelsLike,
                    humidity = weatherDetail.humidity,
                    pressure = weatherDetail.pressure,
                    visibility = weatherDetail.visibility
                )
            }

            Text(text = "Shows now weather details. \n Scroll vertically")
        }
    }
}

@Composable
fun SimpleInfo(
    weather: String,
    temp: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BigLabel(weather)
        BigLabel("${temp}度")
    }
}

@Composable
fun WindInfo(
    modifier: Modifier,
    wind: String,
    dir: String,
    scale: String,
    speed: String
) {
    Column(
        modifier = modifier
    ) {
        ItemDescription(wind, dir)
        ItemDescription("风力", "${scale}级")
        ItemDescription("风速", "${speed}km/h")
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
        ItemDescription("体感温度","${feelsLike}度")
        ItemDescription("相对温度", "${humidity}%")
        ItemDescription("大气压强", "${pressure}hPa")
        ItemDescription("能见度", "${visibility}km")
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
        horizontalArrangement = Arrangement.Start
    ) {
        TitleLabel(title)
        ValueLabel(value, Modifier.fillMaxWidth())
    }
}

@Composable
fun TitleLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
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
fun detailPreview() {
    WeatherDetails(
        Modifier.fillMaxWidth(),
        weatherDetail = fakeWeatherDetail()
    )
}