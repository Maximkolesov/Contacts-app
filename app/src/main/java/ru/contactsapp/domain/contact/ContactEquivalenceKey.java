package ru.contactsapp.domain.contact;

import java.util.List;
import java.util.Objects;

public final class ContactEquivalenceKey {
  private final String name;
  private final List<String> phones;
  private final List<String> emails;

  private ContactEquivalenceKey(String name, List<String> phones, List<String> emails) {
    this.name = Objects.requireNonNull(name, "name");
    this.phones = List.copyOf(phones);
    this.emails = List.copyOf(emails);
  }

  public static ContactEquivalenceKey from(DeviceContact contact) {
    Objects.requireNonNull(contact, "contact");
    return new ContactEquivalenceKey(
        contact.name().normalizedValue(),
        contact.phoneNumbers().values().stream().sorted().toList(),
        contact.emailAddresses().values().stream().sorted().toList());
  }

  public boolean hasComparableData() {
    return !name.isBlank() || !phones.isEmpty() || !emails.isEmpty();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other instanceof ContactEquivalenceKey key) {
      return name.equals(key.name) && phones.equals(key.phones) && emails.equals(key.emails);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, phones, emails);
  }
}
