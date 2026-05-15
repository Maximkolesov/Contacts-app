package ru.contactsapp.domain.duplicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.test.ContactTestFactory;

public final class DuplicateContactFinderTest {
  private final DuplicateContactFinder finder = new DuplicateContactFinder();

  @Test
  public void findGroupsShouldReturnGroupForFullyRepeatedContacts() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of("email-token-alpha"));
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of("EMAIL-TOKEN-ALPHA"));

    List<DuplicateContactGroup> groups = finder.findGroups(List.of(firstContact, secondContact));

    assertEquals(1, groups.size());
    assertEquals(2, groups.get(0).contacts().size());
  }

  @Test
  public void findGroupsShouldIgnoreContactsWithDifferentFilledFields() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of("email-token-alpha"));

    List<DuplicateContactGroup> groups = finder.findGroups(List.of(firstContact, secondContact));

    assertTrue(groups.isEmpty());
  }

  @Test
  public void findGroupsShouldIgnoreBlankContacts() {
    DeviceContact firstContact = ContactTestFactory.contact(1, "", List.of(), List.of());
    DeviceContact secondContact = ContactTestFactory.contact(2, "", List.of(), List.of());

    List<DuplicateContactGroup> groups = finder.findGroups(List.of(firstContact, secondContact));

    assertTrue(groups.isEmpty());
  }

  @Test
  public void findGroupsShouldReturnSeveralGroupsSortedByKeptContact() {
    DeviceContact firstContact = ContactTestFactory.contact(
        5,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        4,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());
    DeviceContact thirdContact = ContactTestFactory.contact(
        2,
        "contact-token-beta",
        List.of("phone-token-002"),
        List.of());
    DeviceContact fourthContact = ContactTestFactory.contact(
        1,
        "contact-token-beta",
        List.of("phone-token-002"),
        List.of());

    List<DuplicateContactGroup> groups = finder.findGroups(
        List.of(firstContact, secondContact, thirdContact, fourthContact));

    assertEquals(2, groups.size());
    assertEquals(new ContactIdentity(1), groups.get(0).contactToKeep().identity());
    assertEquals(new ContactIdentity(4), groups.get(1).contactToKeep().identity());
  }

  @Test
  public void resolutionPlanShouldKeepContactWithMinimalIdentity() {
    DeviceContact firstContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of("phone-token-001"),
        List.of());

    List<DuplicateContactGroup> groups = finder.findGroups(List.of(firstContact, secondContact));
    DuplicateResolutionPlan plan = DuplicateResolutionPlan.fromGroups(groups);

    assertEquals(List.of(new ContactIdentity(1)), plan.contactsToKeep());
    assertEquals(List.of(new ContactIdentity(2)), plan.contactsToRemove());
  }
}
