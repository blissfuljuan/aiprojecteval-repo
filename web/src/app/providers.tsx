import type { ReactNode } from "react";
import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import { AuthProvider } from "@/modules/identity/context/AuthContext";

type AppProvidersProps = {
  children: ReactNode;
};

const theme = createTheme();

export function AppProviders({ children }: AppProvidersProps) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>{children}</AuthProvider>
    </ThemeProvider>
  );
}
