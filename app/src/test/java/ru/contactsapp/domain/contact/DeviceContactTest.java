package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ru.contactsapp.test.ContactTestFactory;

public final class DeviceContactTest {
  @Test
  public void hasComparableDataShouldReturnTrueWhenNameExists() {
    DeviceContact contact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());

    assertTrue(contact.hasComparableData());
  }

  @Test
  public void hasComparableDataShouldReturnTrueWhenPhoneExists() {
    DeviceContact contact = ContactTestFactory.contact(
        1,
        "",
        List.of("phone-token-001"),
        List.of());

    assertTrue(contact.hasComparableData());
  }

  @Test
  public void hasComparableDataShouldReturnTrueWhenEmailExists() {
    DeviceContact contact = ContactTestFactory.contact(
        1,
        "",
        List.of(),
        List.of("email-token-extra"));

    assertTrue(contact.hasComparableData());
  }

  @Test
  public void hasComparableDataShouldReturnFalseForBlankContact() {
    DeviceContact contact = ContactTestFactory.contact(1, "", List.of(), List.of());

    assertFalse(contact.hasComparableData());
  }

  @Test
  public void constructorShouldRejectNullIdentity() {
    assertThrows(
        NullPointerException.class,
        () -> new DeviceContact(
            null,
            new ContactName("contact-token-alpha"),
            new PhoneNumberSet(List.of()),
            new EmailAddressSet(List.of())));
  }
}
