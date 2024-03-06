package net.toughcoder.aeolus.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import net.toughcoder.aeolus.BuildConfig
import net.toughcoder.aeolus.R

@Composable
fun WeatherSectionContainer(
    modifier: Modifier = Modifier,
    @StringRes title: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (title != 0) {
                IconTitleInfo(Modifier.padding(8.dp), title)
            }

            content()
        }
    }
}

@Composable
fun CenteredLoadingContainer(
    modifier: Modifier = Modifier,
    loading: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    Crossfade(
        modifier = modifier.fillMaxSize(),
        targetState = loading,
        label = "crossfade"
    ) { progressing ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (progressing) Alignment.Center else Alignment.TopStart
        ) {
            if (progressing) {
                CircularProgressIndicator(Modifier.size(88.dp))
            } else {
                content()
            }
        }
    }
}

@Composable
fun GeneralText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun IconTitleInfo(
    modifier: Modifier = Modifier,
    @StringRes text: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun TitleLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .paddingFromBaseline(top = 24.dp, bottom = 8.dp),
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
fun AeolusSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .systemBarsPadding()
            // Limit the Snackbar width for large screens
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp),
        snackbar = { Snackbar(it) }
    )
}

@Composable
fun TempBar(
    modifier: Modifier = Modifier,
    max: Float,
    min: Float,
    high: Float,
    low: Float,
    textHigh: String,
    textLow: String = ""
) {
    val height = ((high - low) / (max - min) * HEIGHT).coerceAtLeast(3f)
    val y0 = (max - high) / (max - min) * HEIGHT
    val cw = with(LocalDensity.current) {
        WIDTH.toDp()
    }
    val ch = with(LocalDensity.current) {
        height.toDp()
    }
    val margin = with(LocalDensity.current) {
        y0.toDp()
    }
    val textHeightDp = if (textLow.isNotEmpty()) TEXT_HEIGHT.times(2f) else TEXT_HEIGHT
    val columnHeight = with(LocalDensity.current) {
        HEIGHT.toDp()
    }.plus(textHeightDp)
    val colors = mutableListOf<Color>()
    if (high >= 25f) {
        colors.add(Color.Red)
    }
    colors.add(Color.Green)
    colors.add(Color.Cyan)
    if (low <= 2f) {
        colors.add(Color.Blue)
    }
    Column(
        modifier = Modifier
            .height(columnHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(margin))

        GeneralText(textHigh)

        Canvas(
            modifier = Modifier
                .width(cw)
                .height(ch)
                .align(Alignment.CenterHorizontally)
        ) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = colors
                ),
                topLeft = Offset((size.width - WIDTH) / 2f, 0f),
                size = Size(WIDTH, height),
                cornerRadius = CornerRadius(8.dp.value, 8.dp.value)
            )
        }

        if (textLow.isNotEmpty()) {
            GeneralText(textLow)
        }
    }
}

const val WIDTH = 40f
const val HEIGHT = 120f
val TEXT_HEIGHT = 22.dp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentLocationPermission(
    content: @Composable () -> Unit
) {
    val permState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permState.launchMultiplePermissionRequest()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val context = LocalContext.current

    val gotoSettings = { gotoSettings(context) }

    when {
        permState.allPermissionsGranted -> {
            content()
        }
        permState.shouldShowRationale -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.missing_perm_title)) },
                text = { Text(stringResource(R.string.missing_perm_message)) },
                confirmButton = {
                    TextButton(onClick = { gotoSettings() }) {
                        Text(stringResource(R.string.understood))
                    }
                }
            )
        }
        !permState.allPermissionsGranted && !permState.shouldShowRationale -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.missing_perm_title)) },
                text = { Text(stringResource(R.string.missing_perm_message)) },
                confirmButton = {
                    TextButton(onClick = gotoSettings) {
                        Text(stringResource(R.string.goto_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {}) {
                        Text(stringResource(R.string.understood))
                    }
                }
            )
        }
    }
}

fun gotoSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    intent.data = uri
    context.startActivity(intent)
}