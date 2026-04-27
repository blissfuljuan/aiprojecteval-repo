import { ExternalLink } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Separator } from "@/common/ui/shadcn/separator";
import { Textarea } from "@/common/ui/shadcn/textarea";

export function DeploymentInputCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Deployment Input</CardTitle>
        <CardDescription>Provide the submitted deployment URL and expected endpoints for validation.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="deployment-url">Deployment URL</Label>
          <Input id="deployment-url" placeholder="https://student-project.vercel.app" />
        </div>

        <div className="space-y-2">
          <Label htmlFor="expected-endpoints">Expected Endpoints</Label>
          <Textarea
            id="expected-endpoints"
            className="min-h-32"
            placeholder={"/api/auth/login\n/api/projects\n/api/submissions"}
          />
          <p className="text-sm text-muted-foreground">Add one endpoint per line to prepare validation rules.</p>
        </div>
      </CardContent>
      <Separator />
      <CardFooter className="flex flex-col-reverse gap-3 pt-6 sm:flex-row sm:justify-end">
        <Button type="button" variant="outline" className="w-full sm:w-auto">
          Save Draft
        </Button>
        <Button type="button" className="w-full sm:w-auto">
          <ExternalLink className="h-4 w-4" aria-hidden="true" />
          Validate Now
        </Button>
      </CardFooter>
    </Card>
  );
}
