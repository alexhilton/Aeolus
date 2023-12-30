package net.toughcoder.aeolus.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = if (loading) Alignment.Center else Alignment.TopStart
    ) {
        Crossfade(
            modifier = Modifier,
            targetState = loading,
            label = "crossfade"
        ) { loading ->
            if (loading) {
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
    DisposableEffect(key1 = lifecycleOwner, effect = {
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
    })

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
    intent.setData(uri)
    context.startActivity(intent)
}