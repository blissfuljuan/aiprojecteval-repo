import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

type DocumentEvaluationPlaceholderProps = {
  title: string;
  description: string;
  children: string;
};

export function DocumentEvaluationPlaceholder({
  title,
  description,
  children,
}: DocumentEvaluationPlaceholderProps) {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-normal">{title}</h1>
        <p className="text-sm text-muted-foreground">{description}</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">{title}</CardTitle>
          <CardDescription>Phase 11A foundation</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border border-dashed p-6 text-sm leading-6 text-muted-foreground">{children}</div>
        </CardContent>
      </Card>
    </div>
  );
}
