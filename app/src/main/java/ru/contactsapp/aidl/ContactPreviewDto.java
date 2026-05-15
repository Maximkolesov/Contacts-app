package ru.contactsapp.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public final class ContactPreviewDto implements Parcelable {
  public static final Creator<ContactPreviewDto> CREATOR = new Creator<>() {
    @Override
    public ContactPreviewDto createFromParcel(Parcel parcel) {
      return new ContactPreviewDto(parcel);
    }

    @Override
    public ContactPreviewDto[] newArray(int size) {
      return new ContactPreviewDto[size];
    }
  };

  private final String id;
  private final String displayName;
  private final String primaryPhone;
  private final String sectionLetter;

  public ContactPreviewDto(
      String id,
      String displayName,
      String primaryPhone,
      String sectionLetter) {
    this.id = Objects.requireNonNull(id, "id");
    this.displayName = Objects.requireNonNull(displayName, "displayName");
    this.primaryPhone = Objects.requireNonNull(primaryPhone, "primaryPhone");
    this.sectionLetter = Objects.requireNonNull(sectionLetter, "sectionLetter");
  }

  private ContactPreviewDto(Parcel parcel) {
    id = Objects.requireNonNull(parcel.readString(), "id");
    displayName = Objects.requireNonNull(parcel.readString(), "displayName");
    primaryPhone = Objects.requireNonNull(parcel.readString(), "primaryPhone");
    sectionLetter = Objects.requireNonNull(parcel.readString(), "sectionLetter");
  }

  public String id() {
    return id;
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(id);
    parcel.writeString(displayName);
    parcel.writeString(primaryPhone);
    parcel.writeString(sectionLetter);
  }
}
