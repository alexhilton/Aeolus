package net.toughcoder.aeolus

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.toughcoder.aeolus.data.DataContainer
import net.toughcoder.aeolus.ui.AeolusNavGraph
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(
    dataContainer: DataContainer,
    modifier: Modifier = Modifier
) {
    AeolusTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            AeolusNavGraph(appContainer = dataContainer)
        }
    }
}