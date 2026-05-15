package ru.contactsapp.domain.contact;

import java.util.Locale;
import java.util.Objects;

public final class ContactPreview {
  private final ContactIdentity identity;
  private final String displayName;
  private final String primaryPhone;
  private final String sectionLetter;

  public ContactPreview(ContactIdentity identity, String displayName, String primaryPhone) {
    this.identity = Objects.requireNonNull(identity, "identity");
    this.displayName = requireDisplayName(displayName);
    this.primaryPhone = primaryPhone == null ? "" : primaryPhone;
    sectionLetter = resolveSectionLetter(this.displayName);
  }

  public ContactIdentity identity() {
    return identity;
  }

  public String displayName() {
    return displayName;
  }

  public String primaryPhone() {
    return primaryPhone;
  }

  public String sectionLetter() {
    return sectionLetter;
  }

  private static String requireDisplayName(String value) {
    if (value == null || value.trim().isEmpty()) {
      return "Unnamed contact";
    }

    return value.trim().replaceAll("\\s+", " ");
  }

  private static String resolveSectionLetter(String value) {
    if (value.isBlank()) {
      return "#";
    }

    char first = Character.toUpperCase(value.charAt(0));
    if (Character.isLetter(first)) {
      return Character.toString(first).toUpperCase(Locale.ROOT);
    }

    return "#";
  }
}
