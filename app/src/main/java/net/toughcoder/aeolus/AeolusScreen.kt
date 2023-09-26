package net.toughcoder.aeolus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
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
        Column(modifier = modifier.padding(it)) {
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
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bigLabel(weatherDetail.text)
                bigLabel("${weatherDetail.temp}度")
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp).weight(1.0f)
                ) {
                    ItemDescription(weatherDetail.windDir, "D")
                    ItemDescription("风力", "${weatherDetail.windScale}级")
                    ItemDescription("风速", "${weatherDetail.windSpeed}km/h")
                }

                Column(
                    modifier = Modifier.padding(horizontal = 8.dp).weight(1.0f)
                ) {
                    ItemDescription("体感温度","${weatherDetail.feelsLike}度")
                    ItemDescription("相对温度", "${weatherDetail.humidity}%")
                    ItemDescription("大气压强", "${weatherDetail.pressure}hPa")
                    ItemDescription("能见度", "${weatherDetail.visibility}km")
                }
            }

            Text(text = "Shows now weather details. \n Scroll vertically")
        }
    }
}

@Composable
fun bigLabel(
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