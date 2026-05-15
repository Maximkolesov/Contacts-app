package ru.contactsapp.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import ru.contactsapp.application.port.ContactCatalogPort;
import ru.contactsapp.application.port.ContactPermissionPort;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.exception.ContactAccessException;
import ru.contactsapp.test.ContactTestFactory;

public final class ContactQueryServiceTest {
  @Test
  public void listPreviewsShouldSortBySectionNameAndIdentity() {
    var catalogPort = new FakeContactCatalogPort();
    catalogPort.previews = List.of(
        ContactTestFactory.preview(3, "contact-token-gamma", "phone-token-007"),
        ContactTestFactory.preview(2, "contact-token-beta", "phone-token-004"),
        ContactTestFactory.preview(1, "contact-token-alpha", "phone-token-005"),
        ContactTestFactory.preview(4, "1 contact-token-service", "phone-token-006"));
    var service = new ContactQueryService(catalogPort, new FakeContactPermissionPort(true, false));

    List<ContactPreview> result = service.listPreviews();

    assertEquals(List.of(4L, 1L, 2L, 3L), result.stream()
        .map(ContactPreview::identity)
        .map(ContactIdentity::value)
        .toList());
  }

  @Test
  public void listPreviewsShouldRejectCallWithoutReadPermission() {
    var service = new ContactQueryService(
        new FakeContactCatalogPort(),
        new FakeContactPermissionPort(false, false));

    assertThrows(ContactAccessException.class, service::listPreviews);
  }

  private static final class FakeContactCatalogPort implements ContactCatalogPort {
    private List<ContactPreview> previews = new ArrayList<>();

    @Override
    public List<DeviceContact> findAll() {
      return List.of();
    }

    @Override
    public List<ContactPreview> findAllPreviews() {
      return previews;
    }

    @Override
    public void deleteContacts(Collection<ContactIdentity> identities) {
    }
  }

  private static final class FakeContactPermissionPort implements ContactPermissionPort {
    private final boolean canReadContacts;
    private final boolean canWriteContacts;

    private FakeContactPermissionPort(boolean canReadContacts, boolean canWriteContacts) {
      this.canReadContacts = canReadContacts;
      this.canWriteContacts = canWriteContacts;
    }

    @Override
    public boolean canReadContacts() {
      return canReadContacts;
    }

    @Override
    public boolean canWriteContacts() {
      return canWriteContacts;
    }
  }
}
