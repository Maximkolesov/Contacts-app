package ru.contactsapp.domain.contact;

import java.util.Locale;
import java.util.Objects;

public final class ContactName {
  private final String value;
  private final String normalizedValue;

  public ContactName(String value) {
    this.value = normalizeVisibleValue(value);
    normalizedValue = normalizeComparableValue(this.value);
  }

  public String value() {
    return value;
  }

  public String normalizedValue() {
    return normalizedValue;
  }

  public boolean isBlank() {
    return normalizedValue.isBlank();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other instanceof ContactName name) {
      return normalizedValue.equals(name.normalizedValue);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return normalizedValue.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }

  private static String normalizeVisibleValue(String value) {
    if (value == null) {
      return "";
    }

    return value.trim().replaceAll("\\s+", " ");
  }

  private static String normalizeComparableValue(String value) {
    Objects.requireNonNull(value, "value");
    return value.toLowerCase(Locale.ROOT);
  }
}
