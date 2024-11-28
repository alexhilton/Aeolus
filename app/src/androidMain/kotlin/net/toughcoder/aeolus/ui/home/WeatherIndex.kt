package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.GeneralText
import net.toughcoder.aeolus.ui.TitleLabel
import net.toughcoder.aeolus.ui.ValueLabel
import net.toughcoder.aeolus.ui.WeatherSectionContainer

@Composable
fun WeatherIndexSection(
    modifier: Modifier = Modifier.fillMaxWidth(),
    indices: List<IndexUiState>
) {
    if (indices.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        WeatherSectionContainer(
            modifier = modifier,
            title = R.string.weather_index_title,
            key = indices
        ) {
            WeatherIndexRow(left = indices[0], right = indices[1])

            Divider(thickness = 1.dp, color = Color.Gray)

            WeatherIndexRow(left = indices[2], right = indices[3])

            Divider(thickness = 1.dp, color = Color.Gray)

            WeatherIndexRow(left = indices[4], right = indices[5])
        }
    }
}

@Composable
fun WeatherIndexRow(
    left: IndexUiState,
    right: IndexUiState
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        WeatherIndexItem(Modifier.weight(1f), left)

        Divider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.Gray)

        WeatherIndexItem(Modifier.weight(1f), right)
    }
}

@Composable
fun WeatherIndexItem(
    modifier: Modifier,
    item: IndexUiState
) {
    Column(modifier.padding(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(item.icon),
                contentDescription = item.name
            )
            Column {
                ValueLabel(text = item.name)
                TitleLabel(text = item.category)
            }
        }

        Spacer(Modifier.height(8.dp))

        GeneralText(text = item.text)
    }
}