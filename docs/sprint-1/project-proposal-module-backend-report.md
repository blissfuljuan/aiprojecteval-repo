# Project Proposal Module Backend Report

## Summary

Implemented the backend foundation for project proposals. Students can submit proposals, view their own proposals, update allowed proposal states, and instructors/admins can record final proposal decisions. Approved proposals automatically create a `Project` in the same transaction.

## Implemented Files

- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/model/ProjectProposal.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/model/ProposalStatus.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/dto/ProjectProposalCreateRequest.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/dto/ProjectProposalUpdateRequest.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/dto/ProposalDecisionRequest.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/dto/ProjectProposalResponse.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/repository/ProjectProposalRepository.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/service/ProjectProposalService.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/service/ProjectProposalServiceImpl.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/projectproposal/controller/ProjectProposalController.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/courseclass/model/CourseClass.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/courseclass/repository/CourseClassRepository.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/project/model/Project.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/project/repository/ProjectRepository.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/project/dto/ProjectResponse.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/project/mapper/ProjectMapper.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/security/SecurityConfig.java`
- `backend/src/test/java/com/blissfuljuan/aiprojecteval/projectproposal/service/ProjectProposalServiceImplTest.java`
- `backend/src/test/java/com/blissfuljuan/aiprojecteval/projectproposal/controller/ProjectProposalControllerTest.java`

## Endpoints Added

- `POST /api/project-proposals`
- `GET /api/project-proposals/my`
- `GET /api/project-proposals`
- `GET /api/project-proposals/{id}`
- `PUT /api/project-proposals/{id}`
- `DELETE /api/project-proposals/{id}`
- `PATCH /api/project-proposals/{id}/instructor-decision`

## Business Rules Implemented

- New proposals default to `SUBMITTED`.
- Duplicate active proposals are rejected for the same submitting user.
- Active statuses are `SUBMITTED`, `ADVISER_REVIEW_SCHEDULED`, `ADVISER_REVIEWED`, `INSTRUCTOR_REVIEW_SCHEDULED`, and `APPROVED`.
- Proposals can be updated only in `DRAFT`, `SUBMITTED`, or `REVISION_REQUIRED`.
- Proposals can be deleted only in `DRAFT` or `SUBMITTED`.
- Final decision values are limited to `APPROVED`, `REVISION_REQUIRED`, and `REJECTED`.
- Repeated approval is rejected.
- Approved and rejected proposals are treated as final decisions and cannot be decided again.
- Duplicate project creation for an already-linked proposal is rejected.

## Role-Based Rules

- `STUDENT` and `ADMIN` can create proposals.
- `STUDENT` can access `GET /api/project-proposals/my`.
- `ADMIN`, `INSTRUCTOR`, and `ADVISER` can access `GET /api/project-proposals`.
- `GET`, `PUT`, and `DELETE` by ID require authentication and are checked in the service.
- Only `ADMIN` and `INSTRUCTOR` can call the instructor decision endpoint.
- Students can view and update only their own proposals, subject to status rules.

## Project Auto-Creation Behavior

When a proposal is approved:

- The proposal status becomes `APPROVED`.
- `approvedAt` and `instructorRemarks` are set.
- A `Project` is created in the same transaction.
- Project title comes from proposal title.
- Project description uses proposal expected output when present, otherwise the problem statement.
- Project ownership uses the submitting user.
- The created project stores the proposal ID in nullable `projectProposalId`.

## Tests Added

- Student can create a proposal.
- Proposal defaults to `SUBMITTED`.
- Duplicate active proposal is rejected.
- Student can update a `SUBMITTED` proposal.
- Student cannot update an `APPROVED` proposal.
- Instructor can approve a proposal.
- Approval creates a project automatically.
- Instructor can mark a proposal as `REVISION_REQUIRED`.
- Instructor can reject a proposal.
- Student cannot approve a proposal.
- Invalid decision status is rejected.
- Already approved proposals cannot receive another decision.
- Unauthenticated proposal request is rejected.
- Student cannot access the all-proposals endpoint.
- Instructor can access the instructor-decision endpoint.
- Student cannot access the instructor-decision endpoint.

## Tests Executed

- `.\mvnw.cmd test`

Result: `BUILD SUCCESS`, 66 tests passed, 0 failures, 0 errors.

## Failed Tests

None.

## Remaining TODOs

- Restrict instructors to assigned course classes when class assignment exists.
- Restrict advisers to scheduled proposal consultations when consultation scheduling exists.
- Add StudentGroup ownership and duplicate-proposal checks when a group module exists.
- Add database migrations if the project moves away from Hibernate-managed schema generation.

## Recommended Next Backend Task

Implement the course class ownership/assignment foundation so instructor proposal visibility can be scoped to handled classes instead of allowing all instructor access.
