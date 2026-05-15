package ru.contactsapp.test;

import java.util.List;

import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactName;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.contact.EmailAddressSet;
import ru.contactsapp.domain.contact.PhoneNumberSet;

public final class ContactTestFactory {
  private ContactTestFactory() {
  }

  public static DeviceContact contact(
      long id,
      String name,
      List<String> phones,
      List<String> emails) {
    return new DeviceContact(
        new ContactIdentity(id),
        new ContactName(name),
        new PhoneNumberSet(phones),
        new EmailAddressSet(emails));
  }

  public static ContactPreview preview(long id, String name, String primaryPhone) {
    return new ContactPreview(new ContactIdentity(id), name, primaryPhone);
  }
}
