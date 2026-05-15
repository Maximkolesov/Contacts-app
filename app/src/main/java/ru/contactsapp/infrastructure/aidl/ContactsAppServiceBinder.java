package ru.contactsapp.infrastructure.aidl;

import android.os.Handler;
import android.os.RemoteException;

import java.util.Objects;

import ru.contactsapp.aidl.IContactsAppService;
import ru.contactsapp.aidl.IContactPreviewCallback;
import ru.contactsapp.aidl.IDuplicateCleanupCallback;
import ru.contactsapp.application.service.ContactQueryService;
import ru.contactsapp.application.service.DuplicateContactCleanupService;

public final class ContactsAppServiceBinder extends IContactsAppService.Stub {
  private final Handler workerHandler;
  private final ContactQueryService contactQueryService;
  private final DuplicateContactCleanupService duplicateContactCleanupService;
  private final AidlContactMapper mapper;

  public ContactsAppServiceBinder(
      Handler workerHandler,
      ContactQueryService contactQueryService,
      DuplicateContactCleanupService duplicateContactCleanupService,
      AidlContactMapper mapper) {
    this.workerHandler = Objects.requireNonNull(workerHandler, "workerHandler");
    this.contactQueryService = Objects.requireNonNull(contactQueryService, "contactQueryService");
    this.duplicateContactCleanupService = Objects.requireNonNull(
        duplicateContactCleanupService,
        "duplicateContactCleanupService");
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  @Override
  public void loadContacts(IContactPreviewCallback callback) {
    if (callback == null) {
      return;
    }

    workerHandler.post(() -> loadContactsInternal(callback));
  }

  @Override
  public void cleanDuplicateContacts(IDuplicateCleanupCallback callback) {
    if (callback == null) {
      return;
    }

    workerHandler.post(() -> cleanDuplicateContactsInternal(callback));
  }

  private void loadContactsInternal(IContactPreviewCallback callback) {
    try {
      callback.onContactsLoaded(mapper.toContactPreviewDtos(contactQueryService.listPreviews()));
    } catch (RuntimeException | RemoteException exception) {
      notifyContactPreviewError(callback, exception);
    }
  }

  private void cleanDuplicateContactsInternal(IDuplicateCleanupCallback callback) {
    try {
      var result = duplicateContactCleanupService.cleanDuplicateContacts();
      callback.onCleanupFinished(mapper.toDuplicateCleanupResultDto(result));
    } catch (RuntimeException | RemoteException exception) {
      notifyDuplicateCleanupError(callback, exception);
    }
  }

  private void notifyContactPreviewError(IContactPreviewCallback callback, Exception exception) {
    try {
      callback.onError(resolveMessage(exception));
    } catch (RemoteException ignored) {
    }
  }

  private void notifyDuplicateCleanupError(
      IDuplicateCleanupCallback callback,
      Exception exception) {
    try {
      callback.onError(resolveMessage(exception));
    } catch (RemoteException ignored) {
    }
  }

  private static String resolveMessage(Exception exception) {
    if (exception.getMessage() == null || exception.getMessage().isBlank()) {
      return "Unexpected contact service error";
    }

    return exception.getMessage();
  }
}
