package ru.contactsapp.presentation.state

data class ContactHeaderUiItem(
    val letter: String,
) : ContactUiItem {
    override val key: String = "header-$letter"
}
