package ru.contactsapp.infrastructure.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import ru.contactsapp.application.service.ContactCleanupOperationService;
import ru.contactsapp.application.service.ContactQueryService;
import ru.contactsapp.application.service.DuplicateContactCleanupService;
import ru.contactsapp.domain.duplicate.DuplicateContactFinder;
import ru.contactsapp.infrastructure.android.AndroidContactCatalogAdapter;
import ru.contactsapp.infrastructure.android.AndroidContactMapper;
import ru.contactsapp.infrastructure.android.AndroidContactPermissionAdapter;
import ru.contactsapp.infrastructure.android.ContactPermissionChecker;
import ru.contactsapp.infrastructure.android.ContactsContractDeletionService;
import ru.contactsapp.infrastructure.android.ContactsContractReader;

public final class ContactsAppRemoteService extends Service {
  private HandlerThread workerThread;
  private ContactsAppServiceBinder binder;

  @Override
  public void onCreate() {
    super.onCreate();

    workerThread = new HandlerThread("ContactsAppWorker");
    workerThread.start();

    Handler workerHandler = new Handler(workerThread.getLooper());
    var contactMapper = new AndroidContactMapper();
    var contactCatalogPort = new AndroidContactCatalogAdapter(
        new ContactsContractReader(getContentResolver()),
        new ContactsContractDeletionService(getContentResolver()),
        contactMapper);
    var contactPermissionPort = new AndroidContactPermissionAdapter(
        new ContactPermissionChecker(this));
    var operationService = new ContactCleanupOperationService();

    binder = new ContactsAppServiceBinder(
        workerHandler,
        new ContactQueryService(contactCatalogPort, contactPermissionPort),
        new DuplicateContactCleanupService(
            contactCatalogPort,
            contactPermissionPort,
            new DuplicateContactFinder(),
            operationService),
        new AidlContactMapper());
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public void onDestroy() {
    if (workerThread != null) {
      workerThread.quitSafely();
    }

    super.onDestroy();
  }
}
