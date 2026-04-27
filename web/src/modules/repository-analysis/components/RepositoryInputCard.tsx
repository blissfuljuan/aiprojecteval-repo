import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";

export function RepositoryInputCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Repository Input</CardTitle>
        <CardDescription>
          Analysis will inspect repository structure, commit activity, modules, and supporting code evidence.
        </CardDescription>
      </CardHeader>
      <CardContent className="grid gap-4 md:grid-cols-[minmax(0,1fr)_220px]">
        <div className="space-y-2">
          <Label htmlFor="repository-url">GitHub URL</Label>
          <Input id="repository-url" placeholder="https://github.com/student/project-repository" />
        </div>
        <div className="space-y-2">
          <Label htmlFor="repository-branch">Branch</Label>
          <Select id="repository-branch" defaultValue="main">
            <option value="main">main</option>
            <option value="develop">develop</option>
            <option value="feature/submission">feature/submission</option>
          </Select>
        </div>
      </CardContent>
    </Card>
  );
}
