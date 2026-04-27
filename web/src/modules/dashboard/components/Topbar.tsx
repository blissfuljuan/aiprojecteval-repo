import { Bell, Search } from "lucide-react";
import { Avatar, AvatarFallback } from "@/common/ui/shadcn/avatar";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/common/ui/shadcn/dropdown-menu";
import { Input } from "@/common/ui/shadcn/input";
import { useAuth } from "@/modules/identity/context/AuthContext";

function getDisplayName(firstName?: string, lastName?: string) {
  return [firstName, lastName].filter(Boolean).join(" ") || "User";
}

function getInitials(firstName?: string, lastName?: string) {
  return `${firstName?.charAt(0) ?? ""}${lastName?.charAt(0) ?? ""}`.toUpperCase() || "U";
}

export function Topbar() {
  const { user, logout } = useAuth();
  const displayName = getDisplayName(user?.firstName, user?.lastName);
  const initials = getInitials(user?.firstName, user?.lastName);

  async function handleLogout() {
    await logout();
  }

  return (
    <header className="flex h-16 items-center justify-between border-b bg-background px-6">
      <div className="space-y-0.5">
        <p className="text-sm font-semibold">AI Project Evaluation</p>
        <p className="text-xs text-muted-foreground">Compliance analysis system</p>
      </div>

      <div className="flex items-center gap-3">
        <div className="relative hidden w-80 lg:block">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input placeholder="Search projects, submissions, reports..." className="pl-9" />
        </div>

        <Button variant="ghost" size="icon" aria-label="Notifications">
          <Bell className="h-4 w-4" aria-hidden="true" />
        </Button>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-auto gap-3 px-2 py-1.5">
              <Avatar className="h-9 w-9">
                <AvatarFallback>{initials}</AvatarFallback>
              </Avatar>
              <div className="hidden text-left md:block">
                <p className="text-sm font-medium leading-none">{displayName}</p>
                <Badge variant="secondary" className="mt-1">
                  {user?.role ?? "USER"}
                </Badge>
              </div>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel>{displayName}</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem>Profile</DropdownMenuItem>
            <DropdownMenuItem>Account settings</DropdownMenuItem>
            <DropdownMenuItem onSelect={() => void handleLogout()}>Sign out</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
