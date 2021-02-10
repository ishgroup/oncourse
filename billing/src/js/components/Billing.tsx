import React, {useState} from 'react';
import { ThemeProvider } from '@material-ui/core/styles';
import CustomizedSteppers from '../components/Stepper'
import Header from "../components/common/Header";
import {
  DarkThemeKey,
  DefaultThemeKey,
  MonochromeThemeKey,
  HighcontrastThemeKey,
  ChristmasThemeKey,
} from "../models/Theme";
import { darkTheme, defaultTheme, monochromeTheme, highcontrastTheme, christmasTheme } from "../themes/ishTheme";
import { ThemeContext} from "../themes/ThemeContext";
import MessageProvider from "./common/message/MessageProvider";


const Billing = () => {
  const themeHandler = (name: any) => {
    setThemeName(name);
    setTheme(currentTheme(name));

    localStorage.setItem("theme", name);
  };

  const currentTheme = (themeName: any) => {
    switch (themeName) {
      case DarkThemeKey: {
        return darkTheme;
      }
      case DefaultThemeKey: {
        return defaultTheme;
      }
      case MonochromeThemeKey: {
        return monochromeTheme;
      }
      case HighcontrastThemeKey: {
        return highcontrastTheme;
      }
      case ChristmasThemeKey: {
        return christmasTheme;
      }
      default: {
        return defaultTheme;
      }
    }
  };

  const getTheme = (theme: any) => {
    let actualTheme = theme;

    try {
      const storageThemeName = localStorage.getItem("theme");
      if (storageThemeName) {
        actualTheme = currentTheme(storageThemeName);
      }
    } catch (e) {
      console.error(e);
      return actualTheme;
    }

    return actualTheme;
  };

  const [themeName, setThemeName] = useState(DefaultThemeKey);
  const [theme, setTheme] = useState(getTheme(defaultTheme));

  return (
    <ThemeContext.Provider
      value={{
        themeHandler,
        themeName
      }}
    >
      <ThemeProvider theme={theme}>
        <Header/>
        <CustomizedSteppers />
        <MessageProvider />
      </ThemeProvider>
    </ThemeContext.Provider>
  );
}

export default Billing;
