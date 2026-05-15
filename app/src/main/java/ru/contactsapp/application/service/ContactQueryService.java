package ru.contactsapp.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ru.contactsapp.application.port.ContactCatalogPort;
import ru.contactsapp.application.port.ContactPermissionPort;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.exception.ContactAccessException;

public final class ContactQueryService {
  private final ContactCatalogPort contactCatalogPort;
  private final ContactPermissionPort contactPermissionPort;

  public ContactQueryService(
      ContactCatalogPort contactCatalogPort,
      ContactPermissionPort contactPermissionPort) {
    this.contactCatalogPort = Objects.requireNonNull(contactCatalogPort, "contactCatalogPort");
    this.contactPermissionPort = Objects.requireNonNull(
        contactPermissionPort,
        "contactPermissionPort");
  }

  public List<ContactPreview> listPreviews() {
    if (!contactPermissionPort.canReadContacts()) {
      throw new ContactAccessException("Read contacts permission is not granted");
    }

    return contactCatalogPort.findAllPreviews().stream()
        .sorted(Comparator.comparing(ContactPreview::sectionLetter)
            .thenComparing(ContactPreview::displayName)
            .thenComparing(preview -> preview.identity().value()))
        .toList();
  }
}
