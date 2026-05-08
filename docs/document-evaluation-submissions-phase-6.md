# Document Evaluation Submissions - Phase 6

Phase 6 adds the backend foundation for document submission metadata. It does not implement AI evaluation, text extraction, manual grading, or actual file upload/storage.

## New Domain Records

- `DocumentSubmission`: a draft or submitted attempt for one active `DocumentRequirementSetAssignment` and one `DocumentRequirement`.
- `DocumentSubmissionFile`: metadata for files associated with a submission. URL, storage path, stored name, checksum, and upload timestamp are nullable so storage can be integrated later.

## Endpoints

Base path: `/api/document-evaluation`

- `POST /submissions/draft`
- `POST /submissions`
- `PUT /submissions/{submissionId}/draft`
- `PATCH /submissions/{submissionId}/submit`
- `GET /submissions/my`
- `GET /submissions/{submissionId}`
- `GET /requirement-set-assignments/{assignmentId}/submissions`
- `GET /projects/{projectId}/submissions`
- `GET /classes/{courseClassId}/submissions`
- `PATCH /submissions/{submissionId}/archive`
- `POST /submissions/{submissionId}/files`
- `DELETE /submissions/files/{fileId}`

## Business Rules

- Submissions must target an `ACTIVE` requirement set assignment.
- The selected document requirement must belong to the assignment requirement set.
- Submission context is copied from the assignment: class assignments set `courseClass`; project assignments set `project`.
- Multiple submitted attempts are preserved. New attempts use the previous max attempt number plus one.
- Only one draft can exist for the same submitted user, assignment, and document requirement.
- Phase 6 implements only `DRAFT -> SUBMITTED` and archive transitions.
- File records are metadata only and default to `PENDING_UPLOAD`.
- Allowed file type validation uses the existing `DocumentRequirement.allowedFileTypes` values. Max file size validation is left for the future because no requirement-level size field exists yet.

## Intentionally Deferred

- Actual multipart upload handling
- File storage providers
- Document text extraction
- AI evaluation
- Manual rubric grading and scoring
- Hardcoded document type enums
