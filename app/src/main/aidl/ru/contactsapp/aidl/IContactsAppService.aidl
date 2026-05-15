package ru.contactsapp.aidl;

import ru.contactsapp.aidl.IContactPreviewCallback;
import ru.contactsapp.aidl.IDuplicateCleanupCallback;

interface IContactsAppService {
  void loadContacts(IContactPreviewCallback callback);

  void cleanDuplicateContacts(IDuplicateCleanupCallback callback);
}
