package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R

@Composable
fun HourlyWeatherSection(
    modifier: Modifier = Modifier,
    hourlyUiStates: List<HourlyUiState>
) {
    if (hourlyUiStates.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        HourlyWeatherList(modifier, hourlyUiStates)
    }
}

@Composable
fun HourlyWeatherList(
    modifier: Modifier = Modifier,
    hourlyList: List<HourlyUiState>
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconTitleInfo(Modifier.padding(8.dp), R.string.hourly_forecast_title)

            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hourlyList) {
                    HourlyListItem(modifier, it)
                }
            }
        }
    }
}

@Composable
fun HourlyListItem(
    modifier: Modifier = Modifier,
    hourlyItem: HourlyUiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier
                .size(36.dp),
            painter = painterResource(hourlyItem.icon),
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
        GeneralText(hourlyItem.text)
        GeneralText(hourlyItem.temp)

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(18.dp)
                    .rotate(hourlyItem.windDegree),
                painter = painterResource(hourlyItem.iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            GeneralText(hourlyItem.windScale)
        }

        GeneralText(hourlyItem.time)
    }
}