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
import LoadingIndicator from "./common/Loading";
import BillingService from "../api/services/BillingApi";
import {UserType} from "../models/User";
import {useDispatch} from "react-redux";
import {getSites} from "../redux/actions";


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
  const [userType, setUserType] = React.useState<UserType>(null);
  const dispatch = useDispatch();

  const classes = useStyles();

  React.useEffect(() => {
    const search = new URLSearchParams(window.location.search);
    const user = search.get("user");

    if (user) {
      BillingService.checkUser(user).then(exist => {
        if(exist) {
          setUserType("Existing")
          dispatch(getSites(user));
        } else {
          setUserType("New")
        }
        window.history.replaceState({}, document.title, location.protocol + '//' + location.host + location.pathname);
      })
    } else {
      setUserType("New")
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
          {userType ? <Stepper userType={userType} /> : <LoadingIndicator />}
        </div>
        <MessageProvider />
      </ThemeProvider>
    </ThemeContext.Provider>
  );
}

export default Billing;
