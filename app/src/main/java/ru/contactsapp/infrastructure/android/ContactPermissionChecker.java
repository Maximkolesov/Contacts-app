package ru.contactsapp.infrastructure.android;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Objects;

public final class ContactPermissionChecker {
  private final Context context;

  public ContactPermissionChecker(Context context) {
    this.context = Objects.requireNonNull(context, "context").getApplicationContext();
  }

  public boolean isGranted(String permission) {
    Objects.requireNonNull(permission, "permission");
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
  }
}
