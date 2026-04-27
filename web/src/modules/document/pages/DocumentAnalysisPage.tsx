import { useParams } from "react-router";
import { AlertTriangle, CheckCircle2, ClipboardList, Lightbulb, ListChecks } from "lucide-react";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/common/ui/shadcn/accordion";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { documentService } from "@/modules/document/services/documentService";

export function DocumentAnalysisPage() {
  const { documentId } = useParams();
  const document = documentService.getDocumentById(documentId);
  const analysis = documentService.getAnalysisResult();

  const summaryCards = [
    { label: "Completeness", value: `${analysis.completenessScore}%` },
    { label: "Requirement Coverage", value: `${analysis.requirementCoverage}%` },
    { label: "Structural Accuracy", value: `${analysis.structuralAccuracy}%` },
    { label: "Compliance Rating", value: analysis.complianceRating },
  ];

  const sections = [
    {
      title: "Extracted Key Requirements",
      icon: ListChecks,
      items: [
        "Users shall authenticate before accessing project evaluation records.",
        "Evaluators shall review submitted SRS, SDD, SPMP, and STD documents.",
        "The system shall track analysis status for each uploaded document.",
      ],
    },
    {
      title: "Detected Missing Sections",
      icon: ClipboardList,
      items: [
        "Detailed data dictionary is incomplete.",
        "Risk mitigation table is missing ownership assignments.",
        "Acceptance criteria are absent for several non-functional requirements.",
      ],
    },
    {
      title: "AI Findings",
      icon: CheckCircle2,
      items: analysis.findings,
    },
    {
      title: "Compliance Issues",
      icon: AlertTriangle,
      items: [
        "Traceability matrix does not cover all high-priority requirements.",
        "Testing references do not map to all documented exception flows.",
        "Template section numbering needs review for consistency.",
      ],
    },
    {
      title: "Suggested Improvements",
      icon: Lightbulb,
      items: analysis.suggestions,
    },
  ];

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-normal">Document Analysis</h1>
        <p className="text-sm text-muted-foreground">AI-powered compliance and content analysis.</p>
      </div>

      <Card>
        <CardContent className="flex flex-col gap-2 p-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-medium">{document.fileName}</p>
            <p className="text-sm text-muted-foreground">{document.projectName}</p>
          </div>
          <Badge variant="outline">{document.documentType}</Badge>
        </CardContent>
      </Card>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((item) => (
          <Card key={item.label}>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{item.label}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-semibold">{item.value}</div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        {sections.map((section) => {
          const Icon = section.icon;

          return (
            <Card key={section.title} className={section.title === "Suggested Improvements" ? "xl:col-span-2" : ""}>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-lg">
                  <Icon className="h-5 w-5 text-muted-foreground" aria-hidden="true" />
                  {section.title}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <Accordion>
                  {section.items.map((item, index) => (
                    <AccordionItem key={item} open={index === 0}>
                      <AccordionTrigger>{`${section.title} ${index + 1}`}</AccordionTrigger>
                      <AccordionContent>{item}</AccordionContent>
                    </AccordionItem>
                  ))}
                </Accordion>
                <Separator className="mt-2" />
                <p className="mt-3 text-xs text-muted-foreground">Static placeholder content for layout validation.</p>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
