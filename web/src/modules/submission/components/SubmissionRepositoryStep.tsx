import { Github, Globe2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";

export function SubmissionRepositoryStep() {
  return (
    <div className="grid gap-4">
      <Card className="shadow-none">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Github className="h-4 w-4" aria-hidden="true" />
            Repository
          </CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2">
          <div className="grid gap-2 md:col-span-2">
            <Label htmlFor="github-url">GitHub Repository URL</Label>
            <Input id="github-url" placeholder="https://github.com/sample/campus-clinic" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="branch-name">Branch Name</Label>
            <Input id="branch-name" placeholder="main" />
          </div>
        </CardContent>
      </Card>
      <Card className="shadow-none">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Globe2 className="h-4 w-4" aria-hidden="true" />
            Deployment
          </CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2">
          <div className="grid gap-2 md:col-span-2">
            <Label htmlFor="deployment-url">Deployment URL</Label>
            <Input id="deployment-url" placeholder="https://campus-clinic-demo.vercel.app" />
          </div>
          <div className="grid gap-2 md:col-span-2">
            <Label htmlFor="api-base-url">Optional API Base URL</Label>
            <Input id="api-base-url" placeholder="https://api.example.edu" />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
