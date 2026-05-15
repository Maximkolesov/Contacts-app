package ru.contactsapp.infrastructure.aidl;

import java.util.List;
import java.util.Objects;

import ru.contactsapp.aidl.ContactPreviewDto;
import ru.contactsapp.aidl.DuplicateCleanupResultDto;
import ru.contactsapp.domain.contact.ContactPreview;
import ru.contactsapp.domain.duplicate.DuplicateCleanupResult;

public final class AidlContactMapper {
  public List<ContactPreviewDto> toContactPreviewDtos(List<ContactPreview> previews) {
    Objects.requireNonNull(previews, "previews");
    return previews.stream()
        .map(this::toContactPreviewDto)
        .toList();
  }

  public ContactPreviewDto toContactPreviewDto(ContactPreview preview) {
    Objects.requireNonNull(preview, "preview");
    return new ContactPreviewDto(
        Long.toString(preview.identity().value()),
        preview.displayName(),
        preview.primaryPhone(),
        preview.sectionLetter());
  }

  public DuplicateCleanupResultDto toDuplicateCleanupResultDto(DuplicateCleanupResult result) {
    Objects.requireNonNull(result, "result");
    return new DuplicateCleanupResultDto(
        toStatusCode(result.status()),
        result.deletedContactCount(),
        result.duplicateGroupCount(),
        result.message());
  }

  private static int toStatusCode(String status) {
    if (DuplicateCleanupResult.SUCCESS.equals(status)) {
      return DuplicateCleanupResultDto.STATUS_SUCCESS;
    }

    if (DuplicateCleanupResult.NO_DUPLICATES_FOUND.equals(status)) {
      return DuplicateCleanupResultDto.STATUS_NO_DUPLICATES_FOUND;
    }

    return DuplicateCleanupResultDto.STATUS_ERROR;
  }
}
