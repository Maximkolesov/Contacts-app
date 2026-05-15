package ru.contactsapp.aidl;

import ru.contactsapp.aidl.DuplicateCleanupResultDto;

interface IDuplicateCleanupCallback {
  void onCleanupFinished(in DuplicateCleanupResultDto result);

  void onError(String message);
}
