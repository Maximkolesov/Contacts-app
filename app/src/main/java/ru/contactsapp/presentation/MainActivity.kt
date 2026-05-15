package ru.contactsapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import ru.contactsapp.presentation.screen.ContactListScreen
import ru.contactsapp.presentation.theme.ContactsAppTheme
import ru.contactsapp.presentation.viewmodel.ContactListViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ContactListViewModel>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val refreshContactsRunnable = Runnable {
        if (hasRequiredPermissions()) {
            viewModel.loadContacts()
        }
    }
    private val contactObserver = object : ContentObserver(mainHandler) {
        override fun onChange(selfChange: Boolean) {
            scheduleContactsRefresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerContactObserver()

        setContent {
            ContactsAppTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions(),
                ) { grants ->
                    val hasPermissions = grants[Manifest.permission.READ_CONTACTS] == true &&
                        grants[Manifest.permission.WRITE_CONTACTS] == true
                    viewModel.onPermissionsChanged(hasPermissions)
                }

                LaunchedEffect(Unit) {
                    val hasPermissions = hasRequiredPermissions()
                    viewModel.onPermissionsChanged(hasPermissions)
                    if (!hasPermissions) {
                        permissionLauncher.launch(REQUIRED_PERMISSIONS)
                    }
                }

                ContactListScreen(
                    state = viewModel.uiState,
                    onRefreshClick = viewModel::loadContacts,
                    onDeleteDuplicatesClick = viewModel::cleanDuplicateContacts,
                    onRequestPermissionsClick = {
                        permissionLauncher.launch(REQUIRED_PERMISSIONS)
                    },
                    onDismissDialog = viewModel::dismissDialog,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scheduleContactsRefresh()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(refreshContactsRunnable)
        contentResolver.unregisterContentObserver(contactObserver)
        super.onDestroy()
    }

    private fun registerContactObserver() {
        contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            contactObserver,
        )
        contentResolver.registerContentObserver(
            ContactsContract.RawContacts.CONTENT_URI,
            true,
            contactObserver,
        )
        contentResolver.registerContentObserver(
            ContactsContract.Data.CONTENT_URI,
            true,
            contactObserver,
        )
    }

    private fun scheduleContactsRefresh() {
        mainHandler.removeCallbacks(refreshContactsRunnable)
        mainHandler.postDelayed(refreshContactsRunnable, CONTACT_REFRESH_DELAY_MILLIS)
    }

    private fun hasRequiredPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private companion object {
        const val CONTACT_REFRESH_DELAY_MILLIS = 400L
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
        )
    }
}
