import type { JSX } from "react";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import { HomePage } from "./pages/HomePage";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "./theme";
import { CssBaseline } from "@mui/material";
import { createHashRouter, RouterProvider } from "react-router";
import SocialLayout from "./components/SocialLayout";
import PostCreationPage from "./pages/PostCreationPage";
import UserCreationPage from "./pages/UserCreationPage";
import UserSignInPage from "./pages/UserSignInPage";

function App(): JSX.Element {
  const router = createHashRouter([
    {
      Component: SocialLayout,
      children: [
        {
          path: '/post/new',
          Component: PostCreationPage
        },
        {
          path: '/user/sign-up',
          Component: UserCreationPage
        },
        {
          path: '/user/sign-in',
          Component: UserSignInPage
        },
        {
          path: '*',
          Component: HomePage,
        }
      ]
    }
  ])

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <RouterProvider router={router} />
    </ThemeProvider>
  );
}

export default App;
