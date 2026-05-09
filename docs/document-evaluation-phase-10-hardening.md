# Document Evaluation Phase 10 Hardening

Phase 10 reviewed and tightened the backend submission and document evaluation workflow before frontend integration.

## Summary

- Preserved the existing `submission` module as the single source of truth for submissions, files, attempts, statuses, upload, replace, remove, download, and view behavior.
- Preserved the `documentevaluation` module as the owner of requirement presets, requirement sets, assignments, completeness reports, manual evaluation, rubric scoring, findings, feedback, and publication.
- Continued using `SubmissionQueryService` as the read boundary from document evaluation into submissions.
- Hardened the submission workflow so direct submitted records cannot bypass the draft upload and submit validation flow.
- Hardened evaluation startup so archived or inactive requirement sets cannot be evaluated.

## Final Workflow Rules

- Students create a draft document submission for an active requirement set assignment and a requirement that belongs to that assignment's requirement set.
- Draft submissions can be updated only while they remain `DRAFT`.
- Draft file upload, replace, and remove are allowed only on authorized draft submissions.
- A draft can be submitted only when it has at least one valid uploaded file.
- Submitted, resubmitted, accepted, and archived submissions are not editable as drafts.
- Document completeness is based on the active assignment, active requirement set, latest relevant submissions, and valid uploaded files.
- Required missing or invalid documents block completeness; optional missing documents are reported but do not block completeness.
- Manual document evaluation can start only for `DOCUMENT` submissions in `SUBMITTED`, `RESUBMITTED`, or `ACCEPTED` status.
- Evaluation startup requires an active assignment, an active requirement set, a requirement that belongs to the assigned set, and at least one valid uploaded file.
- Multiple active evaluations for the same submission are blocked.
- Draft and in-progress evaluations can be edited.
- Completed, returned, and archived evaluations are read-only except for allowed publication state changes.
- Scores cannot be negative or exceed the criterion maximum score.
- Selected rubric levels must belong to the selected criterion.
- Only completed or returned evaluations can be published.
- Students can view only their own published completed or returned results.

## Role Access Summary

- `ADMIN` can manage presets, requirement sets, assignments, submissions, evaluations, and publication state.
- `INSTRUCTOR` can manage owned requirement sets, their assignments, and connected submissions or evaluations.
- `EVALUATOR` can work with evaluation endpoints according to the existing evaluation service rules, but does not manage instructor configuration endpoints.
- `STUDENT` can manage their own draft submissions and files, view their own submissions, and view only published evaluation results.
- Project owner behavior remains based on the existing project ownership field. Future project membership and class enrollment checks remain deferred until those records exist.

## Important API Groups

- Submission drafts and lifecycle: `/api/submissions`
- Submission files: `/api/submissions/{submissionId}/files` and `/api/submission-files/{fileId}`
- Requirement presets: `/api/document-evaluation/presets`
- Requirement sets: `/api/document-evaluation/requirement-sets`
- Requirement set assignments: `/api/document-evaluation/requirement-set-assignments`
- Completeness reports: `/api/document-evaluation/.../completeness`
- Manual evaluations and publication: `/api/document-evaluation/evaluations`
- Student published results: `/api/document-evaluation/evaluation-results/my`

## Known TODOs

- Include students with no submissions in class completeness summaries once enrollment or class membership records exist.
- Add deeper MIME sniffing and requirement-specific file size or count limits when those requirement fields exist.
- Add project member checks once a project membership model exists.
- No AI evaluation was implemented in this phase.
- No new document submission model was created.
