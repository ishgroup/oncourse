import React from "react";

const initialValues = {
  themeHandler: (name:string) => {},
  themeName: "default",
}

// @ts-ignore
export const ThemeContext = React.createContext(initialValues);
