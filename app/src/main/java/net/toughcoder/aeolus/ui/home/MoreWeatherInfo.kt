package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.DailyUiState

@Composable
fun DailyWeatherSection(
    modifier: Modifier = Modifier,
    dailyWeathers: List<DailyUiState>,
    gotoMore: () -> Unit
) {
    if (dailyWeathers.isNotEmpty() && dailyWeathers.size >= 3) {
        Spacer(Modifier.height(16.dp))

        DailyWeathersInfo(modifier, dailyWeathers, gotoMore)
    }
}

@Composable
fun DailyWeathersInfo(
    modifier: Modifier = Modifier,
    dailyWeathers: List<DailyUiState>,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DailyWeatherList(modifier, dailyWeathers)

            Spacer(Modifier.height(8.dp))

            Button(onClick = onMoreClick) {
                Text(stringResource(R.string.button_view_more))
            }
        }
    }
}

@Composable
fun DailyWeatherList(
    modifier: Modifier = Modifier,
    dailyWeathers: List<DailyUiState>
) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        DailyWeatherItem(modifier, stringResource(R.string.today), dailyWeathers[0])

        DailyWeatherItem(modifier, stringResource(R.string.tomorrow), dailyWeathers[1])

        DailyWeatherItem(modifier, stringResource(R.string.day_after), dailyWeathers[2])
    }
}

@Composable
fun DailyWeatherItem(
    modifier: Modifier = Modifier,
    title: String,
    weather: DailyUiState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GeneralText(text = title)

            Spacer(Modifier.width(6.dp))

            GeneralText(text = weather.date)

            Spacer(Modifier.width(16.dp))

            GeneralText(text = weather.textDay)

            Spacer(Modifier.width(16.dp))

            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(weather.iconDay),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
        }

        GeneralText(text = "${weather.tempHigh} / ${weather.tempLow}")
    }
}

@Preview
@Composable
fun DailyWeatherItemPreview() {
    DailyWeatherItem(
        Modifier,
        "Today",
        DailyUiState(
            "2023-11-09",
            "29",
            "4",
            "07:21",
            "17:32",
            R.drawable.ic_1002,
            "Cloudy", "13"
        )
    )
}