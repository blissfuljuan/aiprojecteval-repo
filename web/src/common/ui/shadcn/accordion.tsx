import * as React from "react";
import { ChevronDown } from "lucide-react";
import { cn } from "@/common/lib/utils";

const Accordion = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => <div ref={ref} className={cn("divide-y", className)} {...props} />,
);
Accordion.displayName = "Accordion";

const AccordionItem = React.forwardRef<HTMLDetailsElement, React.DetailsHTMLAttributes<HTMLDetailsElement>>(
  ({ className, ...props }, ref) => <details ref={ref} className={cn("group py-1", className)} {...props} />,
);
AccordionItem.displayName = "AccordionItem";

const AccordionTrigger = React.forwardRef<HTMLElement, React.HTMLAttributes<HTMLElement>>(
  ({ className, children, ...props }, ref) => (
    <summary
      ref={ref}
      className={cn(
        "flex cursor-pointer list-none items-center justify-between gap-3 py-3 text-sm font-medium transition-colors hover:text-primary [&::-webkit-details-marker]:hidden",
        className,
      )}
      {...props}
    >
      {children}
      <ChevronDown className="h-4 w-4 shrink-0 text-muted-foreground transition-transform group-open:rotate-180" />
    </summary>
  ),
);
AccordionTrigger.displayName = "AccordionTrigger";

const AccordionContent = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn("pb-4 text-sm text-muted-foreground", className)} {...props} />
  ),
);
AccordionContent.displayName = "AccordionContent";

export { Accordion, AccordionContent, AccordionItem, AccordionTrigger };
