import { Link } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";

export function RegisterPage() {
  return (
    <Card className="w-full shadow-sm">
      <CardHeader className="space-y-2">
        <CardTitle className="text-2xl">Create account</CardTitle>
        <CardDescription>Set up access for the evaluation and compliance analysis workspace.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-5">
          <div className="grid gap-2">
            <Label htmlFor="fullName">Full name</Label>
            <Input id="fullName" type="text" placeholder="Enter your full name" autoComplete="name" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" placeholder="name@university.edu" autoComplete="email" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="role">Role</Label>
            <Select id="role" defaultValue="">
              <option value="" disabled>
                Select a role
              </option>
              <option value="student">Student</option>
              <option value="evaluator">Evaluator</option>
              <option value="adviser">Adviser</option>
            </Select>
          </div>
          <div className="grid gap-2">
            <Label htmlFor="password">Password</Label>
            <Input id="password" type="password" placeholder="Create a password" autoComplete="new-password" />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="confirmPassword">Confirm password</Label>
            <Input
              id="confirmPassword"
              type="password"
              placeholder="Confirm your password"
              autoComplete="new-password"
            />
          </div>
          <Button type="button" className="w-full">
            Create account
          </Button>
        </form>
      </CardContent>
      <Separator />
      <CardFooter className="justify-center p-6">
        <p className="text-sm text-muted-foreground">
          Already have an account?{" "}
          <Link to="/login" className="font-medium text-primary hover:underline">
            Sign in
          </Link>
        </p>
      </CardFooter>
    </Card>
  );
}
