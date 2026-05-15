package ru.contactsapp.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;
import ru.contactsapp.domain.operation.ContactCleanupOperation;
import ru.contactsapp.domain.operation.ContactCleanupOperationStatus;

public final class ContactCleanupOperationServiceTest {
  @Test
  public void startOperationShouldAddOperationToJournal() {
    var service = new ContactCleanupOperationService();

    ContactCleanupOperation operation = service.startOperation();

    assertEquals(1, service.listOperations().size());
    assertEquals(operation.id(), service.listOperations().get(0).id());
  }

  @Test
  public void finishOperationShouldCompleteJournalOperation() {
    var service = new ContactCleanupOperationService();
    ContactCleanupOperation operation = service.startOperation();

    service.finishOperation(operation, DuplicateCleanupResult.success(1, 1));

    assertEquals(ContactCleanupOperationStatus.COMPLETED, operation.status());
    assertFalse(service.listOperations().isEmpty());
  }
}
