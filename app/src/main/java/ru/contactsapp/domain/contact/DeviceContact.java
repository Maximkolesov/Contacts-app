package ru.contactsapp.domain.contact;

import java.util.Objects;

public final class DeviceContact {
  private final ContactIdentity identity;
  private final ContactName name;
  private final PhoneNumberSet phoneNumbers;
  private final EmailAddressSet emailAddresses;

  public DeviceContact(
      ContactIdentity identity,
      ContactName name,
      PhoneNumberSet phoneNumbers,
      EmailAddressSet emailAddresses) {
    this.identity = Objects.requireNonNull(identity, "identity");
    this.name = Objects.requireNonNull(name, "name");
    this.phoneNumbers = Objects.requireNonNull(phoneNumbers, "phoneNumbers");
    this.emailAddresses = Objects.requireNonNull(emailAddresses, "emailAddresses");
  }

  public ContactIdentity identity() {
    return identity;
  }

  public ContactName name() {
    return name;
  }

  public PhoneNumberSet phoneNumbers() {
    return phoneNumbers;
  }

  public EmailAddressSet emailAddresses() {
    return emailAddresses;
  }

  public boolean hasComparableData() {
    return !name.isBlank() || !phoneNumbers.isEmpty() || !emailAddresses.isEmpty();
  }
}
