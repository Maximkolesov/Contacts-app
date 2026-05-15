package ru.contactsapp.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import ru.contactsapp.domain.contact.ContactPreview
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult
import ru.contactsapp.presentation.gateway.AidlContactsAppGateway
import ru.contactsapp.presentation.gateway.ContactsAppGateway
import ru.contactsapp.presentation.state.ContactHeaderUiItem
import ru.contactsapp.presentation.state.ContactListUiState
import ru.contactsapp.presentation.state.ContactPreviewUiItem
import ru.contactsapp.presentation.state.ContactUiItem
import ru.contactsapp.presentation.state.DuplicateCleanupDialogState

class ContactListViewModel(application: Application) : AndroidViewModel(application) {
    private val gateway: ContactsAppGateway = AidlContactsAppGateway(application)

    var uiState by mutableStateOf(ContactListUiState(isLoading = true))
        private set

    init {
        gateway.connect()
    }

    fun onPermissionsChanged(hasPermissions: Boolean) {
        uiState = uiState.copy(hasPermissions = hasPermissions)
        if (hasPermissions) {
            loadContacts()
        } else {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Contacts permissions are required",
            )
        }
    }

    fun loadContacts() {
        if (!uiState.hasPermissions) {
            uiState = uiState.copy(errorMessage = "Contacts permissions are required")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        gateway.loadContacts(object : ContactsAppGateway.ContactListCallback {
            override fun onLoaded(contacts: MutableList<ContactPreview>) {
                uiState = uiState.copy(
                    items = toUiItems(contacts),
                    isLoading = false,
                    errorMessage = null,
                )
            }

            override fun onError(message: String) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = message,
                )
            }
        })
    }

    fun cleanDuplicateContacts() {
        if (!uiState.hasPermissions) {
            uiState = uiState.copy(errorMessage = "Contacts permissions are required")
            return
        }

        uiState = uiState.copy(isCleaning = true, errorMessage = null)
        gateway.cleanDuplicateContacts(object : ContactsAppGateway.DuplicateCleanupCallback {
            override fun onFinished(result: DuplicateCleanupResult) {
                uiState = uiState.copy(
                    isCleaning = false,
                    dialogState = toDialogState(result),
                    errorMessage = null,
                )
                loadContacts()
            }

            override fun onError(message: String) {
                uiState = uiState.copy(
                    isCleaning = false,
                    dialogState = DuplicateCleanupDialogState(
                        title = "Error",
                        message = message,
                    ),
                )
            }
        })
    }

    fun dismissDialog() {
        uiState = uiState.copy(dialogState = null)
    }

    override fun onCleared() {
        gateway.disconnect()
        super.onCleared()
    }

    private fun toUiItems(contacts: List<ContactPreview>): List<ContactUiItem> {
        val items = mutableListOf<ContactUiItem>()
        contacts.groupBy { it.sectionLetter() }
            .toSortedMap()
            .forEach { (letter, group) ->
                items += ContactHeaderUiItem(letter)
                items += group.map {
                    ContactPreviewUiItem(
                        id = it.identity().toString(),
                        displayName = it.displayName(),
                        primaryPhone = it.primaryPhone(),
                    )
                }
            }

        return items
    }

    private fun toDialogState(result: DuplicateCleanupResult): DuplicateCleanupDialogState {
        return when (result.status()) {
            DuplicateCleanupResult.SUCCESS -> DuplicateCleanupDialogState(
                title = "Duplicates removed",
                message = "Deleted contacts: ${result.deletedContactCount()}. " +
                    "Duplicate groups: ${result.duplicateGroupCount()}.",
            )
            DuplicateCleanupResult.NO_DUPLICATES_FOUND -> DuplicateCleanupDialogState(
                title = "No duplicates found",
                message = "No duplicate contacts were found.",
            )
            else -> DuplicateCleanupDialogState(
                title = "Error",
                message = result.message(),
            )
        }
    }
}
