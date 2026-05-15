package ru.contactsapp.domain.duplicate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ru.contactsapp.domain.contact.ContactEquivalenceKey;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.exception.ValidationException;

public final class DuplicateContactGroup {
  private final ContactEquivalenceKey key;
  private final List<DeviceContact> contacts;

  public DuplicateContactGroup(ContactEquivalenceKey key, List<DeviceContact> contacts) {
    this.key = Objects.requireNonNull(key, "key");
    this.contacts = List.copyOf(contacts);
    if (this.contacts.size() < 2) {
      throw new ValidationException("Duplicate group must contain at least two contacts");
    }
  }

  public ContactEquivalenceKey key() {
    return key;
  }

  public List<DeviceContact> contacts() {
    return contacts;
  }

  public DeviceContact contactToKeep() {
    return contacts.stream()
        .min(Comparator.comparing(DeviceContact::identity))
        .orElseThrow(() -> new ValidationException("Duplicate group is empty"));
  }

  public List<DeviceContact> contactsToRemove() {
    DeviceContact contactToKeep = contactToKeep();
    return contacts.stream()
        .filter(contact -> !contact.identity().equals(contactToKeep.identity()))
        .toList();
  }
}
