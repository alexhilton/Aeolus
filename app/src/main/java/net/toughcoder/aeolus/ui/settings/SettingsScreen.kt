package net.toughcoder.aeolus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
        if (uiState.measure != null) {
            SettingsEntry(
                modifier,
                uiState.measure
            ) { k, v ->
                onChange(k, v)
            }
        }
    }
}

@Composable
fun SettingsEntry(
    modifier: Modifier = Modifier,
    entryState: SettingsEntryUiState,
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
                text = stringResource(entryState.title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            DropDownSettingsEntry(
                entryState,
                onSelect = onEntryChange
            )
        }
    }
}

@Composable
fun DropDownSettingsEntry(
    entryState: SettingsEntryUiState,
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
            text = stringResource(entryState.valueTitle()),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            entryState.optionsTitle.forEachIndexed { index, sid ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(sid),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (entryState.selected(index)) {
                                MaterialTheme.colorScheme.inversePrimary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    },
                    onClick = {
                        isExpanded = false
                        onSelect(entryState.key, entryState.options[index])
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