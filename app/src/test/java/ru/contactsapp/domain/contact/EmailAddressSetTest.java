package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public final class EmailAddressSetTest {
  @Test
  public void constructorShouldNormalizeEmails() {
    var emails = new EmailAddressSet(List.of("  EMAIL-TOKEN-ALPHA ", "EMAIL-TOKEN-BETA"));

    assertEquals(Set.of("email-token-alpha", "email-token-beta"), emails.values());
  }

  @Test
  public void constructorShouldRemoveBlankEmails() {
    var emails = new EmailAddressSet(List.of("", "   ", "email-token-alpha"));

    assertEquals(Set.of("email-token-alpha"), emails.values());
  }

  @Test
  public void constructorShouldRemoveDuplicates() {
    var emails = new EmailAddressSet(List.of("EMAIL-TOKEN-ALPHA", "email-token-alpha"));

    assertEquals(Set.of("email-token-alpha"), emails.values());
  }

  @Test
  public void isEmptyShouldReturnTrueForOnlyBlankValues() {
    var emails = new EmailAddressSet(List.of("", "  "));

    assertTrue(emails.isEmpty());
  }
}
