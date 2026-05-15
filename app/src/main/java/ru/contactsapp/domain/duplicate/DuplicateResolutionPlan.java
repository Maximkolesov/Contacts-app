package ru.contactsapp.domain.duplicate;

import java.util.List;
import java.util.Objects;

import ru.contactsapp.domain.contact.ContactIdentity;

public final class DuplicateResolutionPlan {
  private final List<DuplicateContactGroup> groups;
  private final List<ContactIdentity> contactsToKeep;
  private final List<ContactIdentity> contactsToRemove;

  private DuplicateResolutionPlan(
      List<DuplicateContactGroup> groups,
      List<ContactIdentity> contactsToKeep,
      List<ContactIdentity> contactsToRemove) {
    this.groups = List.copyOf(groups);
    this.contactsToKeep = List.copyOf(contactsToKeep);
    this.contactsToRemove = List.copyOf(contactsToRemove);
  }

  public static DuplicateResolutionPlan fromGroups(List<DuplicateContactGroup> groups) {
    Objects.requireNonNull(groups, "groups");

    List<ContactIdentity> contactsToKeep = groups.stream()
        .map(group -> group.contactToKeep().identity())
        .toList();
    List<ContactIdentity> contactsToRemove = groups.stream()
        .flatMap(group -> group.contactsToRemove().stream())
        .map(contact -> contact.identity())
        .toList();

    return new DuplicateResolutionPlan(groups, contactsToKeep, contactsToRemove);
  }

  public List<DuplicateContactGroup> groups() {
    return groups;
  }

  public List<ContactIdentity> contactsToKeep() {
    return contactsToKeep;
  }

  public List<ContactIdentity> contactsToRemove() {
    return contactsToRemove;
  }

  public boolean isEmpty() {
    return contactsToRemove.isEmpty();
  }

  public int duplicateGroupCount() {
    return groups.size();
  }
}
