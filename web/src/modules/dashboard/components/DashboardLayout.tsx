import { Outlet } from "react-router";
import { Sidebar } from "@/modules/dashboard/components/Sidebar";
import { Topbar } from "@/modules/dashboard/components/Topbar";

export function DashboardLayout() {
  return (
    <div className="min-h-screen bg-muted/30">
      <Topbar />
      <div className="flex">
        <Sidebar />
        <main className="min-w-0 flex-1 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
