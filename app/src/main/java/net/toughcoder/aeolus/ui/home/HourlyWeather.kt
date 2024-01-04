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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.GeneralText
import net.toughcoder.aeolus.ui.TempBar
import net.toughcoder.aeolus.ui.WeatherSectionContainer

@Composable
fun HourlyWeatherSection(
    modifier: Modifier = Modifier,
    hourlyUiStates: List<HourlyUiState>
) {
    if (hourlyUiStates.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        val max = hourlyUiStates.maxOfOrNull { it.tempValue }!!
        val min = hourlyUiStates.minOfOrNull { it.tempValue }!!
        WeatherSectionContainer(modifier, R.string.hourly_forecast_title) {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hourlyUiStates) {
                    HourlyListItem(modifier, max, min, it)
                }
            }
        }
    }
}

@Composable
fun HourlyListItem(
    modifier: Modifier = Modifier,
    max: Float,
    min: Float,
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

        TempBar(
            textHigh = hourlyItem.temp,
            max = max,
            min = min,
            high = hourlyItem.tempValue,
            low = min
        )

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