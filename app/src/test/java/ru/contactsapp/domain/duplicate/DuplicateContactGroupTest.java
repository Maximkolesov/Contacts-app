package ru.contactsapp.domain.duplicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Test;

import ru.contactsapp.domain.contact.ContactEquivalenceKey;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.exception.ValidationException;
import ru.contactsapp.test.ContactTestFactory;

public final class DuplicateContactGroupTest {
  @Test
  public void constructorShouldRejectSingleContactGroup() {
    DeviceContact contact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());

    assertThrows(
        ValidationException.class,
        () -> new DuplicateContactGroup(ContactEquivalenceKey.from(contact), List.of(contact)));
  }

  @Test
  public void contactToKeepShouldReturnContactWithMinimalIdentity() {
    DeviceContact firstContact = ContactTestFactory.contact(
        3,
        "contact-token-alpha",
        List.of(),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());
    DeviceContact thirdContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of(),
        List.of());
    var group = new DuplicateContactGroup(
        ContactEquivalenceKey.from(firstContact),
        List.of(firstContact, secondContact, thirdContact));

    assertEquals(new ContactIdentity(1), group.contactToKeep().identity());
  }

  @Test
  public void contactsToRemoveShouldReturnAllContactsExceptKeptContact() {
    DeviceContact firstContact = ContactTestFactory.contact(
        3,
        "contact-token-alpha",
        List.of(),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());
    DeviceContact thirdContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of(),
        List.of());
    var group = new DuplicateContactGroup(
        ContactEquivalenceKey.from(firstContact),
        List.of(firstContact, secondContact, thirdContact));

    List<ContactIdentity> identities = group.contactsToRemove().stream()
        .map(DeviceContact::identity)
        .toList();

    assertEquals(List.of(new ContactIdentity(3), new ContactIdentity(2)), identities);
  }
}
