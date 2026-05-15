package ru.contactsapp.domain.contact;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class EmailAddressSet {
  private final Set<String> values;

  public EmailAddressSet(Collection<String> values) {
    Objects.requireNonNull(values, "values");
    var normalizedValues = new TreeSet<String>();
    values.stream()
        .map(EmailAddressSet::normalize)
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

    if (other instanceof EmailAddressSet emails) {
      return values.equals(emails.values);
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

    return value.trim().toLowerCase(Locale.ROOT);
  }
}
