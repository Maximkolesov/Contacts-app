package ru.contactsapp.domain.duplicate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class DuplicateCleanupResultTest {
  @Test
  public void successShouldContainDeletedCountAndGroupCount() {
    DuplicateCleanupResult result = DuplicateCleanupResult.success(3, 2);

    assertEquals(DuplicateCleanupResult.SUCCESS, result.status());
    assertEquals(3, result.deletedContactCount());
    assertEquals(2, result.duplicateGroupCount());
  }

  @Test
  public void noDuplicatesFoundShouldReturnEmptyCounters() {
    DuplicateCleanupResult result = DuplicateCleanupResult.noDuplicatesFound();

    assertEquals(DuplicateCleanupResult.NO_DUPLICATES_FOUND, result.status());
    assertEquals(0, result.deletedContactCount());
    assertEquals(0, result.duplicateGroupCount());
  }

  @Test
  public void errorShouldKeepMessage() {
    DuplicateCleanupResult result = DuplicateCleanupResult.error("delete-error-token-alpha");

    assertEquals(DuplicateCleanupResult.ERROR, result.status());
    assertEquals("delete-error-token-alpha", result.message());
  }
}
