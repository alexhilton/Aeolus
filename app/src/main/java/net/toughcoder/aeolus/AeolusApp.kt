package net.toughcoder.aeolus

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(modifier: Modifier = Modifier) {
    AeolusScreen(
        modifier,
        viewModel()
    )
}

@Preview
@Composable
fun AeolusScreenPreview() {
    AeolusTheme {
        AeolusApp()
    }
}