import React, {useState} from 'react';
import {createStyles, makeStyles, ThemeProvider} from '@material-ui/core/styles';
import Stepper from './Stepper/Stepper'
import Header from "./common/Header";
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
import {useDispatch} from "react-redux";
import { getCollegeKey, getSites } from "../redux/actions";
import {getCookie} from "../utils";
import { defaultAxios } from '../api/services/DefaultHttpClient';


export const useStyles = makeStyles(() =>
  createStyles({
    root: {
      width: "100%",
      marginTop: "64px",
      height: "calc(100vh - 64px)",
      display: "flex"
    },
  }),
);

const Billing = () => {
  const dispatch = useDispatch();

  const classes = useStyles();

  React.useEffect(() => {
    const token =  getCookie("JSESSIONID");

    if (token) {
      defaultAxios.defaults.withCredentials = true;
      defaultAxios.defaults.baseURL = "https://provisioning.ish.com.au/b/v1";
      defaultAxios.defaults.headers = {
        'Authorization': token
      }
      dispatch(getCollegeKey());
      dispatch(getSites());
    }
  },[]);

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
        <div className={classes.root}>
          <Stepper />
        </div>
        <MessageProvider />
      </ThemeProvider>
    </ThemeContext.Provider>
  );
}

export default Billing;
