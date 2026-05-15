package ru.contactsapp.infrastructure.android;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ru.contactsapp.application.port.ContactCatalogPort;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;

public final class AndroidContactCatalogAdapter implements ContactCatalogPort {
  private final ContactsContractReader reader;
  private final ContactsContractDeletionService deletionService;
  private final AndroidContactMapper mapper;

  public AndroidContactCatalogAdapter(
      ContactsContractReader reader,
      ContactsContractDeletionService deletionService,
      AndroidContactMapper mapper) {
    this.reader = Objects.requireNonNull(reader, "reader");
    this.deletionService = Objects.requireNonNull(deletionService, "deletionService");
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  @Override
  public List<DeviceContact> findAll() {
    return mapper.toDeviceContacts(reader.readContacts());
  }

  @Override
  public List<ContactPreview> findAllPreviews() {
    return mapper.toContactPreviews(reader.readContacts());
  }

  @Override
  public void deleteContacts(Collection<ContactIdentity> identities) {
    deletionService.deleteContacts(identities);
  }
}
