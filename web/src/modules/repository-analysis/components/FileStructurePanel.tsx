import { Separator } from "@/common/ui/shadcn/separator";

const repositoryTree = [
  {
    name: "backend/",
    children: ["src/", "pom.xml"],
  },
  {
    name: "web/",
    children: ["src/", "package.json"],
  },
  {
    name: "docs/",
    children: ["SRS.pdf", "SDD.pdf"],
  },
];

export function FileStructurePanel() {
  return (
    <div className="rounded-md border bg-background p-4">
      <div className="space-y-3 font-mono text-sm">
        {repositoryTree.map((folder, folderIndex) => (
          <div key={folder.name}>
            <div className="font-semibold text-foreground">{folder.name}</div>
            <div className="mt-1 space-y-1 text-muted-foreground">
              {folder.children.map((child) => (
                <div key={`${folder.name}-${child}`} className="pl-4">
                  |-- {child}
                </div>
              ))}
            </div>
            {folderIndex < repositoryTree.length - 1 ? <Separator className="mt-3" /> : null}
          </div>
        ))}
      </div>
    </div>
  );
}
