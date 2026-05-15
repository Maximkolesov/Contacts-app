package ru.contactsapp.presentation.state

data class ContactPreviewUiItem(
    val id: String,
    val displayName: String,
    val primaryPhone: String,
) : ContactUiItem {
    override val key: String = "contact-$id"
}
