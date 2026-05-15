package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class ContactNameTest {
  @Test
  public void constructorShouldNormalizeVisibleValue() {
    var name = new ContactName("  contact-token-alpha   ");

    assertEquals("contact-token-alpha", name.value());
  }

  @Test
  public void constructorShouldNormalizeComparableValue() {
    var name = new ContactName("CONTACT-TOKEN-ALPHA");

    assertEquals("contact-token-alpha", name.normalizedValue());
  }

  @Test
  public void constructorShouldConvertNullToBlankName() {
    var name = new ContactName(null);

    assertTrue(name.isBlank());
    assertEquals("", name.value());
  }

  @Test
  public void equalsShouldUseNormalizedValue() {
    var firstName = new ContactName("contact-token-alpha");
    var secondName = new ContactName("  CONTACT-TOKEN-ALPHA ");

    assertEquals(firstName, secondName);
  }
}
