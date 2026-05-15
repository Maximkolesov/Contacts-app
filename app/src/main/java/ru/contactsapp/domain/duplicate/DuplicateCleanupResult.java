package ru.contactsapp.domain.duplicate;

import java.util.Objects;

public final class DuplicateCleanupResult {
  public static final String SUCCESS = "SUCCESS";
  public static final String NO_DUPLICATES_FOUND = "NO_DUPLICATES_FOUND";
  public static final String ERROR = "ERROR";

  private final String status;
  private final int deletedContactCount;
  private final int duplicateGroupCount;
  private final String message;

  private DuplicateCleanupResult(
      String status,
      int deletedContactCount,
      int duplicateGroupCount,
      String message) {
    this.status = Objects.requireNonNull(status, "status");
    this.deletedContactCount = deletedContactCount;
    this.duplicateGroupCount = duplicateGroupCount;
    this.message = Objects.requireNonNull(message, "message");
  }

  public static DuplicateCleanupResult success(int deletedContactCount, int duplicateGroupCount) {
    return new DuplicateCleanupResult(
        SUCCESS,
        deletedContactCount,
        duplicateGroupCount,
        "Duplicate contacts have been removed");
  }

  public static DuplicateCleanupResult noDuplicatesFound() {
    return new DuplicateCleanupResult(
        NO_DUPLICATES_FOUND,
        0,
        0,
        "Duplicate contacts were not found");
  }

  public static DuplicateCleanupResult error(String message) {
    return new DuplicateCleanupResult(ERROR, 0, 0, message);
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

  public String message() {
    return message;
  }
}
