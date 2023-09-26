package net.toughcoder.aeolus

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(modifier: Modifier = Modifier) {
    AeolusScreen(
        modifier.padding(6.dp),
        viewModel()
    )
}

@Preview(widthDp = 480)
@Composable
fun AeolusScreenPreview() {
    AeolusTheme {
        AeolusApp()
    }
}