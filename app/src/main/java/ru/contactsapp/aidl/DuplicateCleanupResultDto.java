package ru.contactsapp.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public final class DuplicateCleanupResultDto implements Parcelable {
  public static final int STATUS_SUCCESS = 1;
  public static final int STATUS_NO_DUPLICATES_FOUND = 2;
  public static final int STATUS_ERROR = 3;

  public static final Creator<DuplicateCleanupResultDto> CREATOR = new Creator<>() {
    @Override
    public DuplicateCleanupResultDto createFromParcel(Parcel parcel) {
      return new DuplicateCleanupResultDto(parcel);
    }

    @Override
    public DuplicateCleanupResultDto[] newArray(int size) {
      return new DuplicateCleanupResultDto[size];
    }
  };

  private final int statusCode;
  private final int deletedContactCount;
  private final int duplicateGroupCount;
  private final String message;

  public DuplicateCleanupResultDto(
      int statusCode,
      int deletedContactCount,
      int duplicateGroupCount,
      String message) {
    this.statusCode = statusCode;
    this.deletedContactCount = deletedContactCount;
    this.duplicateGroupCount = duplicateGroupCount;
    this.message = Objects.requireNonNull(message, "message");
  }

  private DuplicateCleanupResultDto(Parcel parcel) {
    statusCode = parcel.readInt();
    deletedContactCount = parcel.readInt();
    duplicateGroupCount = parcel.readInt();
    message = Objects.requireNonNull(parcel.readString(), "message");
  }

  public int statusCode() {
    return statusCode;
  }

  public int deletedContactCount() {
    return deletedContactCount;
  }

  public int duplicateGroupCount() {
    return duplicateGroupCount;
  }

  public String message() {
    return message;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeInt(statusCode);
    parcel.writeInt(deletedContactCount);
    parcel.writeInt(duplicateGroupCount);
    parcel.writeString(message);
  }
}
