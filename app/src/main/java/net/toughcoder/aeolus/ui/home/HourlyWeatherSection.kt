package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

@Composable
fun HourlyListItem(
    modifier: Modifier = Modifier,
    hourlyItem: HourlyUiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(hourlyItem.text)
        Text(hourlyItem.temp)
        Text(hourlyItem.time)
    }
}