package ru.contactsapp.application.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import ru.contactsapp.application.port.ContactCatalogPort;
import ru.contactsapp.application.port.ContactPermissionPort;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;
import ru.contactsapp.domain.duplicate.DuplicateContactFinder;
import ru.contactsapp.domain.operation.ContactCleanupOperationStatus;
import ru.contactsapp.test.ContactTestFactory;

public final class DuplicateContactCleanupServiceTest {
  @Test
  public void cleanDuplicateContactsShouldDeleteOnlyRepeatedContacts() {
    var catalogPort = new FakeContactCatalogPort();
    catalogPort.contacts = List.of(
        ContactTestFactory.contact(2, "contact-token-alpha", List.of("phone-token-001"), List.of()),
        ContactTestFactory.contact(1, "contact-token-alpha", List.of("phone-token-001"), List.of()),
        ContactTestFactory.contact(3, "contact-token-beta", List.of("phone-token-002"), List.of()));
    var operationService = new ContactCleanupOperationService();
    var service = service(catalogPort, new FakeContactPermissionPort(true, true), operationService);

    DuplicateCleanupResult result = service.cleanDuplicateContacts();

    assertEquals(DuplicateCleanupResult.SUCCESS, result.status());
    assertEquals(1, result.deletedContactCount());
    assertEquals(1, result.duplicateGroupCount());
    assertEquals(List.of(new ContactIdentity(2)), catalogPort.deletedIdentities);
    assertEquals(
        ContactCleanupOperationStatus.COMPLETED,
        operationService.listOperations().get(0).status());
  }

  @Test
  public void cleanDuplicateContactsShouldReturnNoDuplicatesFoundWithoutDeletion() {
    var catalogPort = new FakeContactCatalogPort();
    catalogPort.contacts = List.of(
        ContactTestFactory.contact(1, "contact-token-alpha", List.of("phone-token-001"), List.of()),
        ContactTestFactory.contact(2, "contact-token-beta", List.of("phone-token-002"), List.of()));
    var service = service(
        catalogPort,
        new FakeContactPermissionPort(true, true),
        new ContactCleanupOperationService());

    DuplicateCleanupResult result = service.cleanDuplicateContacts();

    assertEquals(DuplicateCleanupResult.NO_DUPLICATES_FOUND, result.status());
    assertTrue(catalogPort.deletedIdentities.isEmpty());
  }

  @Test
  public void cleanDuplicateContactsShouldReturnErrorWithoutReadPermission() {
    var operationService = new ContactCleanupOperationService();
    var service = service(
        new FakeContactCatalogPort(),
        new FakeContactPermissionPort(false, true),
        operationService);

    DuplicateCleanupResult result = service.cleanDuplicateContacts();

    assertEquals(DuplicateCleanupResult.ERROR, result.status());
    assertEquals(
        ContactCleanupOperationStatus.FAILED,
        operationService.listOperations().get(0).status());
  }

  @Test
  public void cleanDuplicateContactsShouldReturnErrorWithoutWritePermission() {
    var operationService = new ContactCleanupOperationService();
    var service = service(
        new FakeContactCatalogPort(),
        new FakeContactPermissionPort(true, false),
        operationService);

    DuplicateCleanupResult result = service.cleanDuplicateContacts();

    assertEquals(DuplicateCleanupResult.ERROR, result.status());
    assertEquals(
        ContactCleanupOperationStatus.FAILED,
        operationService.listOperations().get(0).status());
  }

  @Test
  public void cleanDuplicateContactsShouldReturnErrorWhenCatalogFails() {
    var catalogPort = new FakeContactCatalogPort();
    catalogPort.failOnDelete = true;
    catalogPort.contacts = List.of(
        ContactTestFactory.contact(1, "contact-token-alpha", List.of("phone-token-001"), List.of()),
        ContactTestFactory.contact(
            2,
            "contact-token-alpha",
            List.of("phone-token-001"),
            List.of()));
    var operationService = new ContactCleanupOperationService();
    var service = service(catalogPort, new FakeContactPermissionPort(true, true), operationService);

    DuplicateCleanupResult result = service.cleanDuplicateContacts();

    assertEquals(DuplicateCleanupResult.ERROR, result.status());
    assertEquals("delete-error-token-alpha", result.message());
    assertEquals(
        ContactCleanupOperationStatus.FAILED,
        operationService.listOperations().get(0).status());
  }

  private static DuplicateContactCleanupService service(
      ContactCatalogPort catalogPort,
      ContactPermissionPort permissionPort,
      ContactCleanupOperationService operationService) {
    return new DuplicateContactCleanupService(
        catalogPort,
        permissionPort,
        new DuplicateContactFinder(),
        operationService);
  }

  private static final class FakeContactCatalogPort implements ContactCatalogPort {
    private List<DeviceContact> contacts = new ArrayList<>();
    private List<ContactIdentity> deletedIdentities = new ArrayList<>();
    private boolean failOnDelete;

    @Override
    public List<DeviceContact> findAll() {
      return contacts;
    }

    @Override
    public List<ContactPreview> findAllPreviews() {
      return List.of();
    }

    @Override
    public void deleteContacts(Collection<ContactIdentity> identities) {
      if (failOnDelete) {
        throw new IllegalStateException("delete-error-token-alpha");
      }

      deletedIdentities = List.copyOf(identities);
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
