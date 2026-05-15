package ru.contactsapp.infrastructure.android;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public final class AndroidContactMapperTest {
  private final AndroidContactMapper mapper = new AndroidContactMapper();

  @Test
  public void toDeviceContactShouldMapAndroidContactDataToDomainContact() {
    var data = new AndroidContactMapper.ContactData(
        1,
        "  contact-token-alpha  ",
        List.of("phone-token-001"),
        List.of("EMAIL-TOKEN-ALPHA"));

    var contact = mapper.toDeviceContact(data);

    assertEquals(1, contact.identity().value());
    assertEquals("contact-token-alpha", contact.name().value());
    assertEquals(List.of("001"), contact.phoneNumbers().values().stream().toList());
    assertEquals(List.of("email-token-alpha"), contact.emailAddresses().values().stream().toList());
  }

  @Test
  public void toContactPreviewShouldUseFirstPhoneAsPrimaryPhone() {
    var data = new AndroidContactMapper.ContactData(
        1,
        "contact-token-alpha",
        List.of("phone-token-001", "phone-token-002"),
        List.of());

    var preview = mapper.toContactPreview(data);

    assertEquals("contact-token-alpha", preview.displayName());
    assertEquals("phone-token-001", preview.primaryPhone());
  }

  @Test
  public void toContactPreviewShouldUseBlankPhoneWhenDataHasNoPhones() {
    var data = new AndroidContactMapper.ContactData(1, "contact-token-alpha", List.of(), List.of());

    var preview = mapper.toContactPreview(data);

    assertEquals("", preview.primaryPhone());
  }

  @Test
  public void collectionMappingShouldMapAllContacts() {
    var firstData = new AndroidContactMapper.ContactData(
        1,
        "contact-token-alpha",
        List.of(),
        List.of());
    var secondData = new AndroidContactMapper.ContactData(
        2,
        "contact-token-beta",
        List.of(),
        List.of());

    var contacts = mapper.toDeviceContacts(List.of(firstData, secondData));
    var previews = mapper.toContactPreviews(List.of(firstData, secondData));

    assertEquals(2, contacts.size());
    assertEquals(2, previews.size());
  }
}
