package ru.contactsapp.infrastructure.android;

import android.content.ContentResolver;
import android.provider.ContactsContract;

import java.util.Collection;
import java.util.Objects;

import ru.contactsapp.domain.contact.ContactIdentity;
import ru.contactsapp.domain.exception.ContactAccessException;

public final class ContactsContractDeletionService {
  private final ContentResolver contentResolver;

  public ContactsContractDeletionService(ContentResolver contentResolver) {
    this.contentResolver = Objects.requireNonNull(contentResolver, "contentResolver");
  }

  public void deleteContacts(Collection<ContactIdentity> identities) {
    Objects.requireNonNull(identities, "identities");
    for (ContactIdentity identity : identities) {
      deleteContact(identity);
    }
  }

  private void deleteContact(ContactIdentity identity) {
    Objects.requireNonNull(identity, "identity");
    try {
      contentResolver.delete(
          ContactsContract.RawContacts.CONTENT_URI,
          ContactsContract.RawContacts.CONTACT_ID + " = ?",
          new String[] {Long.toString(identity.value())});
    } catch (RuntimeException exception) {
      throw new ContactAccessException("Cannot delete contact: " + identity.value(), exception);
    }
  }
}
