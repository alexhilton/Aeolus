package net.toughcoder.aeolus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.toughcoder.aeolus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.description_back)
                        )
                    }
                }
            )
        }
    ) {
        SettingsSection(Modifier.padding(it), uiState) { k, v ->
            viewModel.updateSettingsEntry(k, v)
        }
    }
}

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onChange: (String, String) -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.language != null) {
            SettingsEntry(
                modifier,
                uiState.language
            ) { k, v ->
                onChange(k, v)
            }
        }
        if (uiState.unit != null) {
            SettingsEntry(
                modifier,
                uiState.unit
            ) { k, v ->
                onChange(k, v)
            }
        }
    }
}

@Composable
fun SettingsEntry(
    modifier: Modifier = Modifier,
    entry: SettingsEntryUiState,
    onEntryChange: (String, String) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(entry.title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

//            Row {
//                Text(
//                    text = entry.value,
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//                Icon(
//                    imageVector = Icons.Default.KeyboardArrowRight,
//                    contentDescription = ""
//                )
//            }

            DropDownSettingsEntry(
                key = entry.key,
                value = entry.value,
                options = entry.options,
                onSelect = onEntryChange
            )
        }
    }
}

@Composable
fun DropDownSettingsEntry(
    key: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onSelect: (String, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(expanded) }

    Row(
        modifier = modifier.clickable { isExpanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            options.forEachIndexed { index, s ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = s,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (s == value) {
                                MaterialTheme.colorScheme.inversePrimary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    },
                    onClick = {
                        isExpanded = false
                        onSelect(key, s)
                    }
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = ""
        )
    }
}