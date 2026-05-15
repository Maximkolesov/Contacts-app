package ru.contactsapp.domain.contact;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class PhoneNumberSet {
  private final Set<String> values;

  public PhoneNumberSet(Collection<String> values) {
    Objects.requireNonNull(values, "values");
    var normalizedValues = new TreeSet<String>();
    values.stream()
        .map(PhoneNumberSet::normalize)
        .filter(value -> !value.isBlank())
        .forEach(normalizedValues::add);

    this.values = Set.copyOf(normalizedValues);
  }

  public Set<String> values() {
    return values;
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other instanceof PhoneNumberSet phoneNumbers) {
      return values.equals(phoneNumbers.values);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  private static String normalize(String value) {
    if (value == null) {
      return "";
    }

    String trimmed = value.trim();
    if (trimmed.startsWith("+")) {
      return "+" + trimmed.substring(1).replaceAll("\\D", "");
    }

    return trimmed.replaceAll("\\D", "");
  }
}
