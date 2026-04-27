import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { identityService } from "@/modules/identity/services/identity.service";
import type { PublicRegistrationRole } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

const publicRoles: PublicRegistrationRole[] = ["INSTRUCTOR", "EVALUATOR", "ADVISER", "STUDENT"];

function splitFullName(fullName: string) {
  const parts = fullName.trim().split(/\s+/);
  const firstName = parts[0] ?? "";
  const lastName = parts.length > 1 ? parts[parts.length - 1] : "";
  const middleName = parts.length > 2 ? parts.slice(1, -1).join(" ") : null;

  return { firstName, middleName, lastName };
}

export function RegisterPage() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState<PublicRegistrationRole | "">("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    const { firstName, middleName, lastName } = splitFullName(fullName);

    if (!firstName || !lastName) {
      setError("Enter at least a first and last name.");
      return;
    }

    if (!email.trim()) {
      setError("Email is required.");
      return;
    }

    if (!role) {
      setError("Select a role.");
      return;
    }

    if (password.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setIsSubmitting(true);

    try {
      await register({
        firstName,
        middleName,
        lastName,
        email: email.trim(),
        password,
        role,
      });
      navigate(paths.dashboard, { replace: true });
    } catch (requestError) {
      setError(identityService.getErrorMessage(requestError));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Card className="w-full shadow-sm">
      <CardHeader className="space-y-2">
        <CardTitle className="text-2xl">Create account</CardTitle>
        <CardDescription>Set up access for the evaluation and compliance analysis workspace.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-5" onSubmit={handleSubmit}>
          {error ? (
            <div className="rounded-md border border-destructive/40 bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {error}
            </div>
          ) : null}
          <div className="grid gap-2">
            <Label htmlFor="fullName">Full name</Label>
            <Input
              id="fullName"
              type="text"
              placeholder="Enter your full name"
              autoComplete="name"
              value={fullName}
              onChange={(event) => setFullName(event.target.value)}
              disabled={isSubmitting}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="name@university.edu"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              disabled={isSubmitting}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="role">Role</Label>
            <Select
              id="role"
              value={role}
              onChange={(event) => setRole(event.target.value as PublicRegistrationRole)}
              disabled={isSubmitting}
            >
              <option value="" disabled>
                Select a role
              </option>
              {publicRoles.map((publicRole) => (
                <option key={publicRole} value={publicRole}>
                  {publicRole.charAt(0) + publicRole.slice(1).toLowerCase()}
                </option>
              ))}
            </Select>
          </div>
          <div className="grid gap-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="Create a password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              disabled={isSubmitting}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="confirmPassword">Confirm password</Label>
            <Input
              id="confirmPassword"
              type="password"
              placeholder="Confirm your password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              disabled={isSubmitting}
            />
          </div>
          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? "Creating account..." : "Create account"}
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
