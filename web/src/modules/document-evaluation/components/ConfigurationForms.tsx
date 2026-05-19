import { useEffect, useMemo, useState } from "react";
import { Button } from "@/common/ui/shadcn/button";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Textarea } from "@/common/ui/shadcn/textarea";
import type {
  AllowedFileType,
  ConfigurationStatus,
  CopyPresetRequest,
  DocumentRequirement,
  DocumentRequirementPreset,
  DocumentRequirementPresetRequest,
  DocumentRequirementRequest,
  DocumentRequirementSetSummary,
  PresetDocumentRequirement,
  PresetDocumentRequirementRequest,
  PresetVisibility,
  RequirementSetAssignmentType,
  RequirementSetRequest,
  UpdateDocumentRequirementRequest,
} from "@/modules/document-evaluation/types";
import type { CourseClass } from "@/modules/project-proposal/types";
import type { Project } from "@/modules/project/types";

export const configurationStatuses: ConfigurationStatus[] = ["DRAFT", "ACTIVE", "ARCHIVED"];
export const presetVisibilities: PresetVisibility[] = ["SYSTEM", "INSTRUCTOR_PRIVATE", "DEPARTMENT_SHARED"];
export const allowedFileTypes: AllowedFileType[] = [
  "PDF",
  "DOC",
  "DOCX",
  "XLS",
  "XLSX",
  "PPT",
  "PPTX",
  "PNG",
  "JPG",
  "JPEG",
  "TXT",
  "ZIP",
];

const visibilityLabels: Record<PresetVisibility, string> = {
  SYSTEM: "System",
  INSTRUCTOR_PRIVATE: "Instructor Private",
  DEPARTMENT_SHARED: "Department Shared",
};

type FormState = {
  name: string;
  description: string;
};

function optionalText(value: string) {
  return value.trim() || undefined;
}

function optionalNumber(value: string) {
  const trimmed = value.trim();
  return trimmed ? Number(trimmed) : undefined;
}

function formatProject(project: Project) {
  return project.title;
}

function formatCourseClass(courseClass: CourseClass) {
  return courseClass.code ? `${courseClass.code} - ${courseClass.name}` : courseClass.name;
}

export function RequirementPresetForm({
  initialPreset,
  isSubmitting,
  onCancel,
  onSubmit,
}: {
  initialPreset?: DocumentRequirementPreset | null;
  isSubmitting: boolean;
  onCancel: () => void;
  onSubmit: (request: DocumentRequirementPresetRequest) => void;
}) {
  const [form, setForm] = useState<DocumentRequirementPresetRequest>({
    name: "",
    description: "",
    category: "",
    visibility: "INSTRUCTOR_PRIVATE",
    status: "DRAFT",
  });
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setForm({
      name: initialPreset?.name ?? "",
      description: initialPreset?.description ?? "",
      category: initialPreset?.category ?? "",
      visibility: initialPreset?.visibility ?? "INSTRUCTOR_PRIVATE",
      status: initialPreset?.status ?? "DRAFT",
    });
    setError(null);
  }, [initialPreset]);

  function set<K extends keyof DocumentRequirementPresetRequest>(field: K, value: DocumentRequirementPresetRequest[K]) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setError("Name is required.");
      return;
    }
    onSubmit({
      name: form.name.trim(),
      description: optionalText(form.description ?? ""),
      category: optionalText(form.category ?? ""),
      visibility: form.visibility,
      status: form.status,
    });
  }

  return (
    <form className="grid gap-4" onSubmit={handleSubmit}>
      {error && <p className="text-sm text-destructive">{error}</p>}
      <div className="grid gap-2">
        <Label htmlFor="preset-name">Name <span aria-hidden="true">*</span></Label>
        <Input id="preset-name" value={form.name} onChange={(event) => set("name", event.target.value)} required />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="preset-description">Description</Label>
        <Textarea
          id="preset-description"
          rows={3}
          value={form.description ?? ""}
          onChange={(event) => set("description", event.target.value)}
        />
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        <div className="grid gap-2">
          <Label htmlFor="preset-category">Category</Label>
          <Input
            id="preset-category"
            value={form.category ?? ""}
            onChange={(event) => set("category", event.target.value)}
          />
        </div>
        <div className="grid gap-2">
          <Label htmlFor="preset-visibility">Visibility <span aria-hidden="true">*</span></Label>
          <Select
            id="preset-visibility"
            value={form.visibility}
            onChange={(event) => set("visibility", event.target.value as PresetVisibility)}
            required
          >
            {presetVisibilities.map((visibility) => (
              <option key={visibility} value={visibility}>{visibilityLabels[visibility]}</option>
            ))}
          </Select>
        </div>
        <div className="grid gap-2">
          <Label htmlFor="preset-status">Status</Label>
          <Select
            id="preset-status"
            value={form.status}
            onChange={(event) => set("status", event.target.value as ConfigurationStatus)}
          >
            {configurationStatuses.map((status) => (
              <option key={status} value={status}>{status}</option>
            ))}
          </Select>
        </div>
      </div>
      <div className="flex flex-wrap justify-end gap-3">
        <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Saving..." : "Save Preset"}</Button>
      </div>
    </form>
  );
}

export function RequirementSetForm({
  initialRequirementSet,
  isSubmitting,
  onCancel,
  onSubmit,
}: {
  initialRequirementSet?: DocumentRequirementSetSummary | null;
  isSubmitting: boolean;
  onCancel: () => void;
  onSubmit: (request: RequirementSetRequest) => void;
}) {
  const [form, setForm] = useState<FormState>({ name: "", description: "" });
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setForm({
      name: initialRequirementSet?.name ?? "",
      description: initialRequirementSet?.description ?? "",
    });
    setError(null);
  }, [initialRequirementSet]);

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setError("Name is required.");
      return;
    }
    onSubmit({
      name: form.name.trim(),
      description: optionalText(form.description),
    });
  }

  return (
    <form className="grid gap-4" onSubmit={handleSubmit}>
      {error && <p className="text-sm text-destructive">{error}</p>}
      <div className="grid gap-2">
        <Label htmlFor="requirement-set-name">Name <span aria-hidden="true">*</span></Label>
        <Input
          id="requirement-set-name"
          value={form.name}
          onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
          required
        />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="requirement-set-description">Description</Label>
        <Textarea
          id="requirement-set-description"
          rows={3}
          value={form.description}
          onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
        />
      </div>
      <div className="flex flex-wrap justify-end gap-3">
        <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Saving..." : "Save Requirement Set"}</Button>
      </div>
    </form>
  );
}

type RequirementFormValue = {
  name: string;
  description: string;
  required: boolean;
  allowedFileTypes: AllowedFileType[];
  sortOrder: string;
  templateId: string;
  rubricId: string;
};

function requirementToForm(requirement?: DocumentRequirement | PresetDocumentRequirement | null): RequirementFormValue {
  const templateId =
    requirement && "templateId" in requirement
      ? requirement.templateId
      : requirement && "template" in requirement
        ? requirement.template?.id
        : undefined;
  const rubricId =
    requirement && "rubricId" in requirement
      ? requirement.rubricId
      : requirement && "rubric" in requirement
        ? requirement.rubric?.id
        : undefined;

  return {
    name: requirement?.name ?? "",
    description: requirement?.description ?? "",
    required: requirement?.required ?? true,
    allowedFileTypes: requirement?.allowedFileTypes ?? [],
    sortOrder: String(requirement?.sortOrder ?? 0),
    templateId: templateId ? String(templateId) : "",
    rubricId: rubricId ? String(rubricId) : "",
  };
}

export function DocumentRequirementForm({
  initialRequirement,
  isSubmitting,
  requireAllowedFileTypes,
  onCancel,
  onSubmit,
}: {
  initialRequirement?: DocumentRequirement | PresetDocumentRequirement | null;
  isSubmitting: boolean;
  requireAllowedFileTypes: boolean;
  onCancel: () => void;
  onSubmit: (request: DocumentRequirementRequest | UpdateDocumentRequirementRequest | PresetDocumentRequirementRequest) => void;
}) {
  const [form, setForm] = useState<RequirementFormValue>(() => requirementToForm(initialRequirement));
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setForm(requirementToForm(initialRequirement));
    setError(null);
  }, [initialRequirement]);

  function toggleFileType(fileType: AllowedFileType) {
    setForm((prev) => ({
      ...prev,
      allowedFileTypes: prev.allowedFileTypes.includes(fileType)
        ? prev.allowedFileTypes.filter((item) => item !== fileType)
        : [...prev.allowedFileTypes, fileType],
    }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setError("Requirement name is required.");
      return;
    }
    const sortOrder = Number(form.sortOrder);
    if (!Number.isInteger(sortOrder) || sortOrder < 0) {
      setError("Sort order must be zero or greater.");
      return;
    }
    if (requireAllowedFileTypes && form.allowedFileTypes.length === 0) {
      setError("Select at least one allowed file type.");
      return;
    }

    onSubmit({
      name: form.name.trim(),
      description: optionalText(form.description),
      required: form.required,
      allowedFileTypes: form.allowedFileTypes,
      sortOrder,
      templateId: optionalNumber(form.templateId),
      rubricId: optionalNumber(form.rubricId),
    });
  }

  return (
    <form className="grid gap-4" onSubmit={handleSubmit}>
      {error && <p className="text-sm text-destructive">{error}</p>}
      <div className="grid gap-2">
        <Label htmlFor="requirement-name">Requirement Name <span aria-hidden="true">*</span></Label>
        <Input
          id="requirement-name"
          value={form.name}
          onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
          required
        />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="requirement-description">Description</Label>
        <Textarea
          id="requirement-description"
          rows={3}
          value={form.description}
          onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
        />
      </div>
      <div className="grid gap-4 md:grid-cols-[minmax(0,1fr)_140px]">
        <label className="flex items-center gap-2 text-sm font-medium">
          <input
            type="checkbox"
            checked={form.required}
            onChange={(event) => setForm((prev) => ({ ...prev, required: event.target.checked }))}
          />
          Required document
        </label>
        <div className="grid gap-2">
          <Label htmlFor="requirement-sort-order">Sort Order <span aria-hidden="true">*</span></Label>
          <Input
            id="requirement-sort-order"
            type="number"
            min={0}
            value={form.sortOrder}
            onChange={(event) => setForm((prev) => ({ ...prev, sortOrder: event.target.value }))}
            required
          />
        </div>
      </div>
      <AllowedFileTypesField selected={form.allowedFileTypes} onToggle={toggleFileType} />
      <div className="grid gap-4 md:grid-cols-2">
        <div className="grid gap-2">
          <Label htmlFor="template-id">Template ID</Label>
          <Input
            id="template-id"
            type="number"
            min={1}
            value={form.templateId}
            onChange={(event) => setForm((prev) => ({ ...prev, templateId: event.target.value }))}
          />
        </div>
        <div className="grid gap-2">
          <Label htmlFor="rubric-id">Rubric ID</Label>
          <Input
            id="rubric-id"
            type="number"
            min={1}
            value={form.rubricId}
            onChange={(event) => setForm((prev) => ({ ...prev, rubricId: event.target.value }))}
          />
        </div>
      </div>
      <div className="flex flex-wrap justify-end gap-3">
        <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Saving..." : "Save Requirement"}</Button>
      </div>
    </form>
  );
}

export function AllowedFileTypesField({
  selected,
  onToggle,
}: {
  selected: AllowedFileType[];
  onToggle: (fileType: AllowedFileType) => void;
}) {
  return (
    <fieldset className="grid gap-2">
      <legend className="text-sm font-medium">Allowed File Types</legend>
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-3 md:grid-cols-4">
        {allowedFileTypes.map((fileType) => (
          <label key={fileType} className="flex items-center gap-2 rounded-md border px-3 py-2 text-sm">
            <input type="checkbox" checked={selected.includes(fileType)} onChange={() => onToggle(fileType)} />
            {fileType}
          </label>
        ))}
      </div>
    </fieldset>
  );
}

export function CopyPresetForm({
  preset,
  isSubmitting,
  onCancel,
  onSubmit,
}: {
  preset: DocumentRequirementPreset;
  isSubmitting: boolean;
  onCancel: () => void;
  onSubmit: (request: CopyPresetRequest) => void;
}) {
  const [form, setForm] = useState<FormState>(() => ({
    name: `${preset.name} Copy`,
    description: preset.description ?? "",
  }));
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setForm({ name: `${preset.name} Copy`, description: preset.description ?? "" });
    setError(null);
  }, [preset]);

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!form.name.trim()) {
      setError("Name is required.");
      return;
    }
    onSubmit({ name: form.name.trim(), description: optionalText(form.description) });
  }

  return (
    <form className="grid gap-4" onSubmit={handleSubmit}>
      {error && <p className="text-sm text-destructive">{error}</p>}
      <div className="grid gap-2">
        <Label htmlFor="copy-name">Requirement Set Name <span aria-hidden="true">*</span></Label>
        <Input
          id="copy-name"
          value={form.name}
          onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
          required
        />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="copy-description">Description</Label>
        <Textarea
          id="copy-description"
          rows={3}
          value={form.description}
          onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
        />
      </div>
      <div className="flex flex-wrap justify-end gap-3">
        <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Copying..." : "Copy to Requirement Set"}</Button>
      </div>
    </form>
  );
}

export function RequirementSetAssignmentForm({
  requirementSets,
  courseClasses,
  projects,
  isSubmitting,
  onSubmit,
}: {
  requirementSets: DocumentRequirementSetSummary[];
  courseClasses: CourseClass[];
  projects: Project[];
  isSubmitting: boolean;
  onSubmit: (request: {
    assignmentType: RequirementSetAssignmentType;
    requirementSetId: number;
    courseClassId?: number;
    projectId?: number;
    notes?: string;
  }) => void;
}) {
  const [assignmentType, setAssignmentType] = useState<RequirementSetAssignmentType>("COURSE_CLASS");
  const [requirementSetId, setRequirementSetId] = useState("");
  const [courseClassId, setCourseClassId] = useState("");
  const [projectId, setProjectId] = useState("");
  const [notes, setNotes] = useState("");
  const [error, setError] = useState<string | null>(null);

  const targetOptions = useMemo(
    () => (assignmentType === "COURSE_CLASS" ? courseClasses : projects),
    [assignmentType, courseClasses, projects],
  );

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const parsedRequirementSetId = Number(requirementSetId);
    const targetId = Number(assignmentType === "COURSE_CLASS" ? courseClassId : projectId);

    if (!parsedRequirementSetId) {
      setError("Requirement set is required.");
      return;
    }
    if (!targetId) {
      setError(assignmentType === "COURSE_CLASS" ? "Course class is required." : "Project is required.");
      return;
    }

    setError(null);
    onSubmit({
      assignmentType,
      requirementSetId: parsedRequirementSetId,
      courseClassId: assignmentType === "COURSE_CLASS" ? targetId : undefined,
      projectId: assignmentType === "PROJECT" ? targetId : undefined,
      notes: optionalText(notes),
    });
  }

  return (
    <form className="grid gap-4" onSubmit={handleSubmit}>
      {error && <p className="text-sm text-destructive">{error}</p>}
      <div className="grid gap-4 md:grid-cols-3">
        <div className="grid gap-2">
          <Label htmlFor="assignment-type">Assignment Type</Label>
          <Select
            id="assignment-type"
            value={assignmentType}
            onChange={(event) => setAssignmentType(event.target.value as RequirementSetAssignmentType)}
          >
            <option value="COURSE_CLASS">Class</option>
            <option value="PROJECT">Project</option>
          </Select>
        </div>
        <div className="grid gap-2">
          <Label htmlFor="assignment-requirement-set">Requirement Set <span aria-hidden="true">*</span></Label>
          <Select
            id="assignment-requirement-set"
            value={requirementSetId}
            onChange={(event) => setRequirementSetId(event.target.value)}
            required
          >
            <option value="">Select a requirement set</option>
            {requirementSets.map((requirementSet) => (
              <option key={requirementSet.id} value={requirementSet.id}>{requirementSet.name}</option>
            ))}
          </Select>
        </div>
        <div className="grid gap-2">
          <Label htmlFor="assignment-target">
            {assignmentType === "COURSE_CLASS" ? "Course Class" : "Project"} <span aria-hidden="true">*</span>
          </Label>
          <Select
            id="assignment-target"
            value={assignmentType === "COURSE_CLASS" ? courseClassId : projectId}
            onChange={(event) => {
              if (assignmentType === "COURSE_CLASS") setCourseClassId(event.target.value);
              else setProjectId(event.target.value);
            }}
            required
          >
            <option value="">Select {assignmentType === "COURSE_CLASS" ? "a course class" : "a project"}</option>
            {targetOptions.map((target) => (
              <option key={target.id} value={target.id}>
                {"code" in target ? formatCourseClass(target) : formatProject(target)}
              </option>
            ))}
          </Select>
        </div>
      </div>
      <div className="grid gap-2">
        <Label htmlFor="assignment-notes">Notes</Label>
        <Textarea id="assignment-notes" rows={3} value={notes} onChange={(event) => setNotes(event.target.value)} />
      </div>
      <div className="flex justify-end">
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Assigning..." : "Create Assignment"}</Button>
      </div>
    </form>
  );
}
