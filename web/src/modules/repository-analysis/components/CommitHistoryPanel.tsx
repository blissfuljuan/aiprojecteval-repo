import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";

const commits = [
  {
    commit: "a1b2c3d",
    message: "Initial project setup",
    author: "student@example.com",
    date: "Apr 10, 2026",
  },
  {
    commit: "d4e5f6g",
    message: "Added authentication module",
    author: "student@example.com",
    date: "Apr 12, 2026",
  },
  {
    commit: "h7i8j9k",
    message: "Implemented submission upload",
    author: "student@example.com",
    date: "Apr 15, 2026",
  },
];

export function CommitHistoryPanel() {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Commit</TableHead>
          <TableHead>Message</TableHead>
          <TableHead>Author</TableHead>
          <TableHead>Date</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {commits.map((commit) => (
          <TableRow key={commit.commit}>
            <TableCell className="font-mono text-xs font-medium">{commit.commit}</TableCell>
            <TableCell>{commit.message}</TableCell>
            <TableCell className="text-muted-foreground">{commit.author}</TableCell>
            <TableCell className="text-muted-foreground">{commit.date}</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
