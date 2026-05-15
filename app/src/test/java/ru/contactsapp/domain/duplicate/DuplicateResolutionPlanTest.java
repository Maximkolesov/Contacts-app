package ru.contactsapp.domain.duplicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ru.contactsapp.domain.contact.ContactEquivalenceKey;
import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.test.ContactTestFactory;

public final class DuplicateResolutionPlanTest {
  @Test
  public void fromGroupsShouldBuildContactsToKeepAndRemove() {
    DeviceContact firstContact = ContactTestFactory.contact(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());
    DeviceContact secondContact = ContactTestFactory.contact(
        2,
        "contact-token-alpha",
        List.of(),
        List.of());
    var group = new DuplicateContactGroup(
        ContactEquivalenceKey.from(firstContact),
        List.of(firstContact, secondContact));

    DuplicateResolutionPlan plan = DuplicateResolutionPlan.fromGroups(List.of(group));

    assertEquals(List.of(new ContactIdentity(1)), plan.contactsToKeep());
    assertEquals(List.of(new ContactIdentity(2)), plan.contactsToRemove());
    assertEquals(1, plan.duplicateGroupCount());
    assertFalse(plan.isEmpty());
  }

  @Test
  public void fromGroupsShouldBuildEmptyPlanForEmptyGroupList() {
    DuplicateResolutionPlan plan = DuplicateResolutionPlan.fromGroups(List.of());

    assertTrue(plan.contactsToKeep().isEmpty());
    assertTrue(plan.contactsToRemove().isEmpty());
    assertTrue(plan.isEmpty());
    assertEquals(0, plan.duplicateGroupCount());
  }
}
