package ru.contactsapp.domain.contact;

import java.util.Objects;

import ru.contactsapp.domain.exception.ValidationException;

public final class ContactIdentity implements Comparable<ContactIdentity> {
  private final long value;

  public ContactIdentity(long value) {
    if (value <= 0) {
      throw new ValidationException("Contact identity must be positive");
    }

    this.value = value;
  }

  public long value() {
    return value;
  }

  @Override
  public int compareTo(ContactIdentity other) {
    Objects.requireNonNull(other, "other");
    return Long.compare(value, other.value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other instanceof ContactIdentity identity) {
      return value == identity.value;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(value);
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
