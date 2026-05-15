package ru.contactsapp.application.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;
import ru.contactsapp.domain.operation.ContactCleanupOperation;

public final class ContactCleanupOperationService {
  private final List<ContactCleanupOperation> operations = new ArrayList<>();

  public synchronized ContactCleanupOperation startOperation() {
    var operation = new ContactCleanupOperation(UUID.randomUUID(), Instant.now());
    operations.add(operation);
    return operation;
  }

  public synchronized void finishOperation(
      ContactCleanupOperation operation,
      DuplicateCleanupResult result) {
    operation.complete(result);
  }

  public synchronized List<ContactCleanupOperation> listOperations() {
    return List.copyOf(operations);
  }
}
