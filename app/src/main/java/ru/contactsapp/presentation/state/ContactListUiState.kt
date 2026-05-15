package ru.contactsapp.presentation.state

data class ContactListUiState(
    val items: List<ContactUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val isCleaning: Boolean = false,
    val hasPermissions: Boolean = false,
    val errorMessage: String? = null,
    val dialogState: DuplicateCleanupDialogState? = null,
)
