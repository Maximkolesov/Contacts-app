package ru.contactsapp.infrastructure.android;

import android.Manifest;

import java.util.Objects;

import ru.contactsapp.application.port.ContactPermissionPort;

public final class AndroidContactPermissionAdapter implements ContactPermissionPort {
  private final ContactPermissionChecker permissionChecker;

  public AndroidContactPermissionAdapter(ContactPermissionChecker permissionChecker) {
    this.permissionChecker = Objects.requireNonNull(permissionChecker, "permissionChecker");
  }

  @Override
  public boolean canReadContacts() {
    return permissionChecker.isGranted(Manifest.permission.READ_CONTACTS);
  }

  @Override
  public boolean canWriteContacts() {
    return permissionChecker.isGranted(Manifest.permission.WRITE_CONTACTS);
  }
}
