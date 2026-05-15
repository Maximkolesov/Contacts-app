package ru.contactsapp.domain.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.contactsapp.domain.exception.ValidationException;

public final class ContactIdentityTest {
  @Test
  public void constructorShouldSavePositiveValue() {
    var identity = new ContactIdentity(42);

    assertEquals(42, identity.value());
  }

  @Test
  public void constructorShouldRejectZeroValue() {
    assertThrows(ValidationException.class, () -> new ContactIdentity(0));
  }

  @Test
  public void compareToShouldSortByValue() {
    var firstIdentity = new ContactIdentity(1);
    var secondIdentity = new ContactIdentity(2);

    assertTrue(firstIdentity.compareTo(secondIdentity) < 0);
  }

  @Test
  public void equalsShouldUseIdentityValue() {
    var firstIdentity = new ContactIdentity(7);
    var secondIdentity = new ContactIdentity(7);

    assertEquals(firstIdentity, secondIdentity);
    assertEquals(firstIdentity.hashCode(), secondIdentity.hashCode());
  }
}
