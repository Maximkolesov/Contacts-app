package ru.contactsapp.domain.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.util.UUID;

import org.junit.Test;

import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;

public final class ContactCleanupOperationTest {
  @Test
  public void constructorShouldCreateRunningOperation() {
    var operation = new ContactCleanupOperation(UUID.randomUUID(), Instant.now());

    assertEquals(ContactCleanupOperationStatus.RUNNING, operation.status());
    assertNull(operation.finishedAt());
    assertEquals("", operation.errorMessage());
  }

  @Test
  public void completeShouldMarkSuccessfulOperationAsCompleted() {
    var operation = new ContactCleanupOperation(UUID.randomUUID(), Instant.now());

    operation.complete(DuplicateCleanupResult.success(2, 1));

    assertEquals(ContactCleanupOperationStatus.COMPLETED, operation.status());
    assertEquals(2, operation.deletedContactCount());
    assertEquals(1, operation.duplicateGroupCount());
    assertEquals("", operation.errorMessage());
    assertNotNull(operation.finishedAt());
  }

  @Test
  public void completeShouldMarkErrorOperationAsFailed() {
    var operation = new ContactCleanupOperation(UUID.randomUUID(), Instant.now());

    operation.complete(DuplicateCleanupResult.error("access-error-token-alpha"));

    assertEquals(ContactCleanupOperationStatus.FAILED, operation.status());
    assertEquals("access-error-token-alpha", operation.errorMessage());
    assertNotNull(operation.finishedAt());
  }
}
