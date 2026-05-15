package ru.contactsapp.infrastructure.android;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.contact.ContactName;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.contact.DeviceContact;
import ru.contactsapp.domain.contact.EmailAddressSet;
import ru.contactsapp.domain.contact.PhoneNumberSet;

public final class AndroidContactMapper {
  public DeviceContact toDeviceContact(ContactData data) {
    Objects.requireNonNull(data, "data");
    return new DeviceContact(
        new ContactIdentity(data.id()),
        new ContactName(data.displayName()),
        new PhoneNumberSet(data.phones()),
        new EmailAddressSet(data.emails()));
  }

  public ContactPreview toContactPreview(ContactData data) {
    Objects.requireNonNull(data, "data");
    String primaryPhone = data.phones().stream().findFirst().orElse("");
    return new ContactPreview(new ContactIdentity(data.id()), data.displayName(), primaryPhone);
  }

  public List<DeviceContact> toDeviceContacts(Collection<ContactData> values) {
    Objects.requireNonNull(values, "values");
    return values.stream().map(this::toDeviceContact).toList();
  }

  public List<ContactPreview> toContactPreviews(Collection<ContactData> values) {
    Objects.requireNonNull(values, "values");
    return values.stream().map(this::toContactPreview).toList();
  }

  public static final class ContactData {
    private final long id;
    private final String displayName;
    private final List<String> phones;
    private final List<String> emails;

    public ContactData(long id, String displayName, List<String> phones, List<String> emails) {
      this.id = id;
      this.displayName = displayName == null ? "" : displayName;
      this.phones = List.copyOf(phones);
      this.emails = List.copyOf(emails);
    }

    public long id() {
      return id;
    }

    public String displayName() {
      return displayName;
    }

    public List<String> phones() {
      return phones;
    }

    public List<String> emails() {
      return emails;
    }
  }
}
