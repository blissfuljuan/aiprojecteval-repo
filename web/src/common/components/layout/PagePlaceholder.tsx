import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

type PagePlaceholderProps = {
  title: string;
  description: string;
};

export function PagePlaceholder({ title, description }: PagePlaceholderProps) {
  return (
    <section className="mx-auto flex min-h-screen w-full max-w-6xl flex-col gap-6 px-6 py-8">
      <Card>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border border-dashed p-6 text-sm text-muted-foreground">
            Module placeholder. Business logic will be added in a later implementation phase.
          </div>
        </CardContent>
      </Card>
    </section>
  );
}
