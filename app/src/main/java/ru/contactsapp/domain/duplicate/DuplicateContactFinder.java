package ru.contactsapp.domain.duplicate;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.contactsapp.domain.contact.ContactEquivalenceKey;
import ru.contactsapp.domain.contact.DeviceContact;

public final class DuplicateContactFinder {
  public List<DuplicateContactGroup> findGroups(Collection<DeviceContact> contacts) {
    Objects.requireNonNull(contacts, "contacts");

    return contacts.stream()
        .filter(DeviceContact::hasComparableData)
        .collect(Collectors.groupingBy(ContactEquivalenceKey::from))
        .entrySet()
        .stream()
        .filter(entry -> entry.getKey().hasComparableData())
        .filter(entry -> entry.getValue().size() > 1)
        .map(entry -> new DuplicateContactGroup(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(group -> group.contactToKeep().identity()))
        .toList();
  }
}
