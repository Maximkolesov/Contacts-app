package ru.contactsapp.application.port;

public interface ContactPermissionPort {
  boolean canReadContacts();

  boolean canWriteContacts();
}
