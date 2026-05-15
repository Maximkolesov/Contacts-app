package ru.contactsapp.domain.operation;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;

public final class ContactCleanupOperation {
  private final UUID id;
  private final Instant startedAt;
  private Instant finishedAt;
  private String status;
  private int deletedContactCount;
  private int duplicateGroupCount;
  private String errorMessage;

  public ContactCleanupOperation(UUID id, Instant startedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.startedAt = Objects.requireNonNull(startedAt, "startedAt");
    status = ContactCleanupOperationStatus.RUNNING;
    errorMessage = "";
  }

  public UUID id() {
    return id;
  }

  public Instant startedAt() {
    return startedAt;
  }

  public Instant finishedAt() {
    return finishedAt;
  }

  public String status() {
    return status;
  }

  public int deletedContactCount() {
    return deletedContactCount;
  }

  public int duplicateGroupCount() {
    return duplicateGroupCount;
  }

  public String errorMessage() {
    return errorMessage;
  }

  public void complete(DuplicateCleanupResult result) {
    Objects.requireNonNull(result, "result");
    finishedAt = Instant.now();
    deletedContactCount = result.deletedContactCount();
    duplicateGroupCount = result.duplicateGroupCount();
    if (DuplicateCleanupResult.ERROR.equals(result.status())) {
      status = ContactCleanupOperationStatus.FAILED;
      errorMessage = result.message();
      return;
    }

    status = ContactCleanupOperationStatus.COMPLETED;
  }
}
