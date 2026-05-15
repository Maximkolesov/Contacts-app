package ru.contactsapp.presentation.gateway;

import java.util.List;

import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;

public interface ContactsAppGateway {
  void connect();

  void disconnect();

  boolean isConnected();

  void loadContacts(ContactListCallback callback);

  void cleanDuplicateContacts(DuplicateCleanupCallback callback);

  interface ContactListCallback {
    void onLoaded(List<ContactPreview> contacts);

    void onError(String message);
  }

  interface DuplicateCleanupCallback {
    void onFinished(DuplicateCleanupResult result);

    void onError(String message);
  }
}
