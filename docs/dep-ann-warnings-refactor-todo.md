# TODO: Dep-Ann Warnings Cleanup

## Input
- Source command: `./gradlew clean assemble`
- Warning class: `[dep-ann] deprecated item is not annotated with @Deprecated`
- Unique warning points: 11

## Tasks
- [x] Review each flagged declaration and confirm intended deprecation status.
- [x] Add `@Deprecated` where Javadoc already marks API as deprecated.
- [x] Remove stale `@deprecated` Javadoc tags where API should remain active.
- [x] Fix warning in `MetadataBlockDataPicture.java:356`.
- [x] Fix warnings in ID3 tag constructors:
  - [x] `ID3v24Tag.java:648`
  - [x] `ID3v1Tag.java:214`
  - [x] `ID3v23Tag.java:372`
  - [x] `ID3v22Tag.java:214`
  - [x] `ID3v11Tag.java:265`
- [x] Fix warnings in ID3 frame constructors:
  - [x] `ID3v24Frame.java:362`
  - [x] `ID3v23Frame.java:312`
  - [x] `ID3v22Frame.java:295`
- [x] Fix warnings in `FrameBodyUnsupported.java`:
  - [x] `FrameBodyUnsupported.java:52`
  - [x] `FrameBodyUnsupported.java:84`
- [x] Run verification build: `./gradlew clean assemble`.
- [x] Confirm no `[dep-ann]` warnings in build output.
- [x] (Optional) Run `./gradlew :libs:lintDebug` as a safety check.

## Done Definition
- [x] Build is green and warning class `[dep-ann]` is fully eliminated for `assemble`.
- [x] Changes are limited to deprecation metadata alignment (no behavioral changes).

## Result
- `./gradlew clean assemble` passed.
- `[dep-ann]` warnings are removed from both debug and release compilation stages.
- Remaining compiler notes are informational (`deprecation`, `unchecked`) and not `dep-ann` warnings.
