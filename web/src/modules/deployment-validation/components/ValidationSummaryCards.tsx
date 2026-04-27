import { CheckCircle, Clock, Globe, Server, ShieldCheck } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const validationSummary = [
  {
    title: "URL Reachable",
    value: "Passed",
    badge: "Success",
    icon: Globe,
    badgeClassName: "border-emerald-200 bg-emerald-50 text-emerald-700",
  },
  {
    title: "HTTP Status",
    value: "200 OK",
    badge: "Healthy",
    icon: Server,
    badgeClassName: "border-emerald-200 bg-emerald-50 text-emerald-700",
  },
  {
    title: "Response Time",
    value: "328 ms",
    badge: "Acceptable",
    icon: Clock,
    badgeClassName: "border-amber-200 bg-amber-50 text-amber-700",
  },
  {
    title: "Security Check",
    value: "HTTPS Enabled",
    badge: "Secure",
    icon: ShieldCheck,
    badgeClassName: "border-emerald-200 bg-emerald-50 text-emerald-700",
  },
];

export function ValidationSummaryCards() {
  return (
    <section className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {validationSummary.map((item) => {
        const Icon = item.icon;

        return (
          <Card key={item.title}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{item.title}</CardTitle>
              <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center gap-2 text-2xl font-semibold">
                {item.value === "Passed" ? <CheckCircle className="h-5 w-5 text-emerald-600" aria-hidden="true" /> : null}
                <span>{item.value}</span>
              </div>
              <Badge variant="outline" className={item.badgeClassName}>
                {item.badge}
              </Badge>
            </CardContent>
          </Card>
        );
      })}
    </section>
  );
}
