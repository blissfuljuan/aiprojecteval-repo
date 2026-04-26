import type { ReactNode } from "react";
import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";

type AppProvidersProps = {
  children: ReactNode;
};

const theme = createTheme();

function AuthProvider({ children }: AppProvidersProps) {
  return <>{children}</>;
}

export function AppProviders({ children }: AppProvidersProps) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>{children}</AuthProvider>
    </ThemeProvider>
  );
}
