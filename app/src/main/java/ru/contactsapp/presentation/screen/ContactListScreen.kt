package ru.contactsapp.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.contactsapp.presentation.state.ContactHeaderUiItem
import ru.contactsapp.presentation.state.ContactListUiState
import ru.contactsapp.presentation.state.ContactPreviewUiItem
import ru.contactsapp.presentation.state.ContactUiItem

@Composable
fun ContactListScreen(
    state: ContactListUiState,
    onRefreshClick: () -> Unit,
    onDeleteDuplicatesClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Contacts",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Find and remove repeated contacts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (!state.hasPermissions) {
                PermissionCard(onRequestPermissionsClick = onRequestPermissionsClick)
            } else {
                ContactContent(
                    modifier = Modifier.weight(1f),
                    state = state,
                    onRefreshClick = onRefreshClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.hasPermissions && !state.isLoading && !state.isCleaning,
                onClick = onDeleteDuplicatesClick,
            ) {
                Text(
                    text = if (state.isCleaning) {
                        "Deleting duplicate contacts..."
                    } else {
                        "Delete duplicate contacts"
                    },
                )
            }
        }

        if (state.isLoading || state.isCleaning) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    state.dialogState?.let { dialogState ->
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(text = dialogState.title) },
            text = { Text(text = dialogState.message) },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text(text = "OK")
                }
            },
        )
    }
}

@Composable
private fun PermissionCard(onRequestPermissionsClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Contact access is required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "The service must read contacts and delete exact duplicates.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(onClick = onRequestPermissionsClick) {
                Text(text = "Grant permissions")
            }
        }
    }
}

@Composable
private fun ContactContent(
    modifier: Modifier = Modifier,
    state: ContactListUiState,
    onRefreshClick: () -> Unit,
) {
    if (state.errorMessage != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = state.errorMessage)
                OutlinedButton(onClick = onRefreshClick) {
                    Text(text = "Try again")
                }
            }
        }
        return
    }

    if (!state.isLoading && state.items.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "No contacts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = "There are no contacts available on this device.")
                OutlinedButton(onClick = onRefreshClick) {
                    Text(text = "Refresh")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        items(
            items = state.items,
            key = ContactUiItem::key,
        ) { item ->
            when (item) {
                is ContactHeaderUiItem -> ContactHeader(item)
                is ContactPreviewUiItem -> ContactRow(item)
            }
        }
    }
}

@Composable
private fun ContactHeader(item: ContactHeaderUiItem) {
    Text(
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
        text = item.letter,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ContactRow(item: ContactPreviewUiItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.displayName.firstOrNull()?.uppercase() ?: "#",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            ) {
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.primaryPhone.ifBlank { "No phone number" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
