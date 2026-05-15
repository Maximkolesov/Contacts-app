package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class ContactPreviewTest {
  @Test
  public void constructorShouldNormalizeDisplayName() {
    var preview = new ContactPreview(
        new ContactIdentity(1),
        "  contact-token-alpha  ",
        "phone-token-007");

    assertEquals("contact-token-alpha", preview.displayName());
  }

  @Test
  public void constructorShouldUseDefaultNameForBlankDisplayName() {
    var preview = new ContactPreview(new ContactIdentity(1), "  ", "");

    assertEquals("Unnamed contact", preview.displayName());
  }

  @Test
  public void sectionLetterShouldUseFirstLetter() {
    var preview = new ContactPreview(new ContactIdentity(1), "contact-token-alpha", "");

    assertEquals("C", preview.sectionLetter());
  }

  @Test
  public void sectionLetterShouldUseHashForDigitPrefix() {
    var preview = new ContactPreview(new ContactIdentity(1), "1 contact-token-alpha", "");

    assertEquals("#", preview.sectionLetter());
  }

  @Test
  public void constructorShouldConvertNullPhoneToBlankPhone() {
    var preview = new ContactPreview(new ContactIdentity(1), "contact-token-alpha", null);

    assertEquals("", preview.primaryPhone());
  }
}
