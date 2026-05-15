package ru.contactsapp.application.service;

import java.util.Objects;

import ru.contactsapp.application.port.ContactCatalogPort;
import ru.contactsapp.application.port.ContactPermissionPort;
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;
import ru.contactsapp.domain.duplicate.DuplicateContactFinder;
import ru.contactsapp.domain.duplicate.DuplicateResolutionPlan;
import ru.contactsapp.domain.exception.ContactAccessException;

public final class DuplicateContactCleanupService {
  private final ContactCatalogPort contactCatalogPort;
  private final ContactPermissionPort contactPermissionPort;
  private final DuplicateContactFinder duplicateContactFinder;
  private final ContactCleanupOperationService operationService;

  public DuplicateContactCleanupService(
      ContactCatalogPort contactCatalogPort,
      ContactPermissionPort contactPermissionPort,
      DuplicateContactFinder duplicateContactFinder,
      ContactCleanupOperationService operationService) {
    this.contactCatalogPort = Objects.requireNonNull(contactCatalogPort, "contactCatalogPort");
    this.contactPermissionPort = Objects.requireNonNull(
        contactPermissionPort,
        "contactPermissionPort");
    this.duplicateContactFinder = Objects.requireNonNull(
        duplicateContactFinder,
        "duplicateContactFinder");
    this.operationService = Objects.requireNonNull(operationService, "operationService");
  }

  public DuplicateCleanupResult cleanDuplicateContacts() {
    var operation = operationService.startOperation();
    DuplicateCleanupResult result;
    try {
      result = cleanDuplicateContactsInternal();
    } catch (RuntimeException exception) {
      result = DuplicateCleanupResult.error(exception.getMessage());
    }

    operationService.finishOperation(operation, result);
    return result;
  }

  private DuplicateCleanupResult cleanDuplicateContactsInternal() {
    if (!contactPermissionPort.canReadContacts()) {
      throw new ContactAccessException("Read contacts permission is not granted");
    }

    if (!contactPermissionPort.canWriteContacts()) {
      throw new ContactAccessException("Write contacts permission is not granted");
    }

    var contacts = contactCatalogPort.findAll();
    var groups = duplicateContactFinder.findGroups(contacts);
    var resolutionPlan = DuplicateResolutionPlan.fromGroups(groups);

    if (resolutionPlan.isEmpty()) {
      return DuplicateCleanupResult.noDuplicatesFound();
    }

    contactCatalogPort.deleteContacts(resolutionPlan.contactsToRemove());
    return DuplicateCleanupResult.success(
        resolutionPlan.contactsToRemove().size(),
        resolutionPlan.duplicateGroupCount());
  }
}
