package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public final class PhoneNumberSetTest {
  @Test
  public void constructorShouldNormalizePhoneNumbers() {
    var phoneNumbers = new PhoneNumberSet(List.of("phone-token-003", "phone-token-004"));

    assertEquals(Set.of("003", "004"), phoneNumbers.values());
  }

  @Test
  public void constructorShouldRemoveBlankNumbers() {
    var phoneNumbers = new PhoneNumberSet(List.of("", "   ", "phone-token-005"));

    assertEquals(Set.of("005"), phoneNumbers.values());
  }

  @Test
  public void constructorShouldRemoveDuplicates() {
    var phoneNumbers = new PhoneNumberSet(List.of("phone-token-003", "phone-token-003"));

    assertEquals(Set.of("003"), phoneNumbers.values());
  }

  @Test
  public void isEmptyShouldReturnTrueForOnlyBlankValues() {
    var phoneNumbers = new PhoneNumberSet(List.of("", "  "));

    assertTrue(phoneNumbers.isEmpty());
  }
}
