package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ru.contactsapp.test.ContactTestFactory;

public final class ContactEquivalenceKeyTest {
  @Test
  public void fromShouldBuildSameKeyForEqualNormalizedContacts() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-003"),
        List.of("EMAIL-TOKEN-ALPHA"));
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "  CONTACT-TOKEN-ALPHA ",
        List.of("phone-token-003"),
        List.of("email-token-alpha"));

    assertEquals(
        ContactEquivalenceKey.from(firstContact),
        ContactEquivalenceKey.from(secondContact));
  }

  @Test
  public void fromShouldIgnoreOrderOfPhonesAndEmails() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-001", "phone-token-002"),
        List.of("email-token-beta", "email-token-alpha"));
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of("phone-token-002", "phone-token-001"),
        List.of("EMAIL-TOKEN-ALPHA", "EMAIL-TOKEN-BETA"));

    assertEquals(
        ContactEquivalenceKey.from(firstContact),
        ContactEquivalenceKey.from(secondContact));
  }

  @Test
  public void fromShouldBuildDifferentKeyForAdditionalFilledField() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of("email-token-extra"));

    assertNotEquals(
        ContactEquivalenceKey.from(firstContact),
        ContactEquivalenceKey.from(secondContact));
  }

  @Test
  public void hasComparableDataShouldReturnFalseForBlankContact() {
    DeviceContact contact = ContactTestFactory.contact(1, "", List.of(), List.of());

    assertFalse(ContactEquivalenceKey.from(contact).hasComparableData());
  }

  @Test
  public void hasComparableDataShouldReturnTrueForPhoneOnlyContact() {
    DeviceContact contact = ContactTestFactory.contact(
        1,
        "",
        List.of("phone-token-001"),
        List.of());

    assertTrue(ContactEquivalenceKey.from(contact).hasComparableData());
  }
}
