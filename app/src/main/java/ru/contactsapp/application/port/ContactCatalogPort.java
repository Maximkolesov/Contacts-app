package ru.contactsapp.application.port;

import java.util.Collection;
import java.util.List;

import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;

public interface ContactCatalogPort {
  List<DeviceContact> findAll();

  List<ContactPreview> findAllPreviews();

  void deleteContacts(Collection<ContactIdentity> identities);
}
