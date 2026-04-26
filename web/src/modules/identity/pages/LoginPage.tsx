import { Link } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Separator } from "@/common/ui/shadcn/separator";

export function LoginPage() {
  return (
    <Card className="w-full shadow-sm">
      <CardHeader className="space-y-2">
        <CardTitle className="text-2xl">Sign in</CardTitle>
        <CardDescription>Access the project evaluation workspace.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="grid gap-5">
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" placeholder="name@university.edu" autoComplete="email" />
          </div>
          <div className="grid gap-2">
            <div className="flex items-center justify-between gap-4">
              <Label htmlFor="password">Password</Label>
              <a href="#" className="text-sm font-medium text-primary hover:underline">
                Forgot password?
              </a>
            </div>
            <Input id="password" type="password" placeholder="Enter your password" autoComplete="current-password" />
          </div>
          <Button type="button" className="w-full">
            Sign in
          </Button>
        </form>
      </CardContent>
      <Separator />
      <CardFooter className="justify-center p-6">
        <p className="text-sm text-muted-foreground">
          New to ProjectEval?{" "}
          <Link to="/register" className="font-medium text-primary hover:underline">
            Create an account
          </Link>
        </p>
      </CardFooter>
    </Card>
  );
}
