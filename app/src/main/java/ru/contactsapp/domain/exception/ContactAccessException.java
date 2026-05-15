package ru.contactsapp.domain.exception;

public final class ContactAccessException extends RuntimeException {
  public ContactAccessException(String message) {
    super(message);
  }

  public ContactAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
