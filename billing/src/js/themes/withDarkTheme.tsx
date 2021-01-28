import React from "react";
import { ThemeProvider } from "@material-ui/styles";
import { darkTheme } from "./ishTheme";

const withDarkTheme = (Component:any) => (props: any) => (
  <ThemeProvider theme={darkTheme}>
    <Component {...props} />
  </ThemeProvider>
);

export default withDarkTheme;
