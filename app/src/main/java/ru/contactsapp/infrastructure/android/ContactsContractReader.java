package ru.contactsapp.infrastructure.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.contactsapp.domain.exception.ContactAccessException;
import ru.contactsapp.infrastructure.android.AndroidContactMapper.ContactData;

public final class ContactsContractReader {
  private final ContentResolver contentResolver;

  public ContactsContractReader(ContentResolver contentResolver) {
    this.contentResolver = Objects.requireNonNull(contentResolver, "contentResolver");
  }

  public Collection<ContactData> readContacts() {
    Map<Long, MutableContactData> contacts = readBaseContacts();
    readPhoneNumbers(contacts);
    readEmails(contacts);

    return contacts.values().stream()
        .map(MutableContactData::toContactData)
        .toList();
  }

  private Map<Long, MutableContactData> readBaseContacts() {
    Map<Long, MutableContactData> contacts = new HashMap<>();
    String[] projection = {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    try (Cursor cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        null,
        null,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC")) {
      if (cursor == null) {
        return contacts;
      }

      int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
      int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

      while (cursor.moveToNext()) {
        long id = cursor.getLong(idIndex);
        String name = cursor.getString(nameIndex);
        contacts.put(id, new MutableContactData(id, name));
      }
    } catch (RuntimeException exception) {
      throw new ContactAccessException("Cannot read contacts", exception);
    }

    return contacts;
  }

  private void readPhoneNumbers(Map<Long, MutableContactData> contacts) {
    String[] projection = {
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    try (Cursor cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        null,
        null,
        null)) {
      if (cursor == null) {
        return;
      }

      int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
      int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

      while (cursor.moveToNext()) {
        long contactId = cursor.getLong(idIndex);
        MutableContactData contact = contacts.get(contactId);
        if (contact != null) {
          contact.addPhone(cursor.getString(numberIndex));
        }
      }
    } catch (RuntimeException exception) {
      throw new ContactAccessException("Cannot read contact phone numbers", exception);
    }
  }

  private void readEmails(Map<Long, MutableContactData> contacts) {
    String[] projection = {
        ContactsContract.CommonDataKinds.Email.CONTACT_ID,
        ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    try (Cursor cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        projection,
        null,
        null,
        null)) {
      if (cursor == null) {
        return;
      }

      int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
      int emailIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);

      while (cursor.moveToNext()) {
        long contactId = cursor.getLong(idIndex);
        MutableContactData contact = contacts.get(contactId);
        if (contact != null) {
          contact.addEmail(cursor.getString(emailIndex));
        }
      }
    } catch (RuntimeException exception) {
      throw new ContactAccessException("Cannot read contact email addresses", exception);
    }
  }

  private static final class MutableContactData {
    private final long id;
    private final String displayName;
    private final List<String> phones = new ArrayList<>();
    private final List<String> emails = new ArrayList<>();

    private MutableContactData(long id, String displayName) {
      this.id = id;
      this.displayName = displayName;
    }

    private void addPhone(String phone) {
      if (phone != null && !phone.isBlank()) {
        phones.add(phone);
      }
    }

    private void addEmail(String email) {
      if (email != null && !email.isBlank()) {
        emails.add(email);
      }
    }

    private ContactData toContactData() {
      return new ContactData(id, displayName, phones, emails);
    }
  }
}
