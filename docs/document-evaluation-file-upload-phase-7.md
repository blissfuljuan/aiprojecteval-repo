# Document Evaluation File Upload - Phase 7

Phase 7 adds actual backend upload and local storage for `DocumentSubmissionFile` records.

## Storage

- Provider: `local`
- Abstraction: `DocumentFileStorageService`
- Implementation: `LocalDocumentFileStorageService`
- Default root: `uploads/document-evaluation`

Config properties:

- `document-evaluation.storage.provider`
- `document-evaluation.storage.local-root`
- `document-evaluation.storage.max-file-size`

The local storage path groups files by requirement set, assignment, and submission, and stored filenames use a UUID plus a sanitized filename to avoid collisions.

## Endpoints

Base path: `/api/document-evaluation`

- `POST /submissions/{submissionId}/files/upload`
- `POST /submissions/{submissionId}/files/upload-multiple`
- `GET /submissions/{submissionId}/files`
- `GET /submissions/files/{fileId}/download`
- `GET /submissions/files/{fileId}/view`
- `DELETE /submissions/files/{fileId}`
- `POST /submissions/files/{fileId}/replace`

## Validation

- Only `DRAFT` submissions accept upload, remove, and replace operations.
- `SUBMITTED` and `ARCHIVED` submissions are read-only for file changes.
- Students can update only their own draft submissions.
- Admins and owning instructors follow the existing assignment-management authorization rules.
- Downloads/views require access to the parent submission.
- Files must be non-empty and under the configured global max size.
- Allowed extensions come from `DocumentRequirement.allowedFileTypes`.
- Filenames are sanitized and path traversal attempts are rejected.
- SHA-256 checksum is computed while storing the file.

## Intentionally Not Implemented

- Cloud storage
- AI evaluation
- Document text extraction
- Manual rubric scoring
- Frontend integration
- Hardcoded document names or document type enums
