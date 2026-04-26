import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Textarea } from "@/common/ui/shadcn/textarea";

export function SubmissionBasicInfoStep() {
  return (
    <div className="grid gap-5">
      <div className="grid gap-2">
        <Label htmlFor="project">Project</Label>
        <Select id="project" defaultValue="campus-clinic">
          <option value="campus-clinic">Campus Clinic Appointment System</option>
          <option value="library-kiosk">Library Kiosk Reservation System</option>
          <option value="inventory-audit">Inventory Audit Tracker</option>
        </Select>
      </div>
      <div className="grid gap-2">
        <Label htmlFor="submission-title">Submission Title</Label>
        <Input id="submission-title" placeholder="Final documentation package" />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="submission-type">Submission Type</Label>
        <Select id="submission-type" defaultValue="initial">
          <option value="initial">Initial Evaluation</option>
          <option value="revision">Revision Submission</option>
          <option value="final">Final Compliance Review</option>
        </Select>
      </div>
      <div className="grid gap-2">
        <Label htmlFor="notes">Notes / Description</Label>
        <Textarea id="notes" placeholder="Add context for reviewers or note the scope covered by this submission." />
      </div>
    </div>
  );
}
