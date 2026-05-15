package ru.contactsapp.aidl;

import ru.contactsapp.aidl.ContactPreviewDto;

interface IContactPreviewCallback {
  void onContactsLoaded(in List<ContactPreviewDto> contacts);

  void onError(String message);
}
