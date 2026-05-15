package ru.contactsapp.presentation.gateway;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.contactsapp.aidl.ContactPreviewDto;
import ru.contactsapp.aidl.DuplicateCleanupResultDto;
import ru.contactsapp.aidl.IContactsAppService;
import ru.contactsapp.aidl.IContactPreviewCallback;
import ru.contactsapp.aidl.IDuplicateCleanupCallback;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;
import ru.contactsapp.infrastructure.aidl.ContactsAppRemoteService;

public final class AidlContactsAppGateway implements ContactsAppGateway {
  private final Context context;
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  private final List<Runnable> pendingCalls = new ArrayList<>();
  private final ServiceConnection serviceConnection = new ContactsAppServiceConnection();
  private IContactsAppService service;
  private boolean bindingStarted;

  public AidlContactsAppGateway(Context context) {
    this.context = Objects.requireNonNull(context, "context").getApplicationContext();
  }

  @Override
  public void connect() {
    if (bindingStarted || service != null) {
      return;
    }

    bindingStarted = true;
    Intent intent = new Intent(context, ContactsAppRemoteService.class);
    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  public void disconnect() {
    if (!bindingStarted) {
      return;
    }

    context.unbindService(serviceConnection);
    bindingStarted = false;
    service = null;
    pendingCalls.clear();
  }

  @Override
  public boolean isConnected() {
    return service != null;
  }

  @Override
  public void loadContacts(ContactListCallback callback) {
    Objects.requireNonNull(callback, "callback");
    runWhenConnected(() -> loadContactsInternal(callback));
  }

  @Override
  public void cleanDuplicateContacts(DuplicateCleanupCallback callback) {
    Objects.requireNonNull(callback, "callback");
    runWhenConnected(() -> cleanDuplicateContactsInternal(callback));
  }

  private void runWhenConnected(Runnable call) {
    if (service != null) {
      call.run();
      return;
    }

    pendingCalls.add(call);
    connect();
  }

  private void loadContactsInternal(ContactListCallback callback) {
    try {
      service.loadContacts(new IContactPreviewCallback.Stub() {
        @Override
        public void onContactsLoaded(List<ContactPreviewDto> contacts) {
          mainHandler.post(() -> callback.onLoaded(toContactPreviews(contacts)));
        }

        @Override
        public void onError(String message) {
          mainHandler.post(() -> callback.onError(message));
        }
      });
    } catch (RemoteException exception) {
      callback.onError(resolveMessage(exception));
    }
  }

  private void cleanDuplicateContactsInternal(DuplicateCleanupCallback callback) {
    try {
      service.cleanDuplicateContacts(new IDuplicateCleanupCallback.Stub() {
        @Override
        public void onCleanupFinished(DuplicateCleanupResultDto result) {
          mainHandler.post(() -> callback.onFinished(toDuplicateCleanupResult(result)));
        }

        @Override
        public void onError(String message) {
          mainHandler.post(() -> callback.onError(message));
        }
      });
    } catch (RemoteException exception) {
      callback.onError(resolveMessage(exception));
    }
  }

  private List<ContactPreview> toContactPreviews(List<ContactPreviewDto> dtos) {
    if (dtos == null) {
      return List.of();
    }

    return dtos.stream()
        .map(this::toContactPreview)
        .toList();
  }

  private ContactPreview toContactPreview(ContactPreviewDto dto) {
    return new ContactPreview(
        new ContactIdentity(Long.parseLong(dto.id())),
        dto.displayName(),
        dto.primaryPhone());
  }

  private DuplicateCleanupResult toDuplicateCleanupResult(DuplicateCleanupResultDto dto) {
    if (dto.statusCode() == DuplicateCleanupResultDto.STATUS_SUCCESS) {
      return DuplicateCleanupResult.success(dto.deletedContactCount(), dto.duplicateGroupCount());
    }

    if (dto.statusCode() == DuplicateCleanupResultDto.STATUS_NO_DUPLICATES_FOUND) {
      return DuplicateCleanupResult.noDuplicatesFound();
    }

    return DuplicateCleanupResult.error(dto.message());
  }

  private static String resolveMessage(Exception exception) {
    if (exception.getMessage() == null || exception.getMessage().isBlank()) {
      return "Contact service is not available";
    }

    return exception.getMessage();
  }

  private final class ContactsAppServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
      service = IContactsAppService.Stub.asInterface(binder);
      for (Runnable call : List.copyOf(pendingCalls)) {
        call.run();
      }

      pendingCalls.clear();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      service = null;
    }
  }
}
