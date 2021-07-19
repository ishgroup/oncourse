import React, { useState } from 'react';
import { ThemeProvider } from '@material-ui/core/styles';
import { useDispatch } from 'react-redux';
import Stepper from './stepper/Stepper';
import Header from './common/Header';
import {
  ChristmasThemeKey,
  DarkThemeKey,
  DefaultThemeKey,
  HighcontrastThemeKey,
  MonochromeThemeKey,
} from '../models/Theme';
import { christmasTheme, darkTheme, defaultTheme, highcontrastTheme, monochromeTheme } from '../themes/ishTheme';
import { ThemeContext } from '../themes/ThemeContext';
import MessageProvider from './common/message/MessageProvider';
import { getCollegeKey, getSites } from '../redux/actions';
import { getCookie } from '../utils';
import { defaultAxios } from '../api/services/DefaultHttpClient';

const Billing = () => {
  const dispatch = useDispatch();

  React.useEffect(() => {
    const token = getCookie('JSESSIONID');
    if (token) {
      defaultAxios.defaults.withCredentials = true;
      defaultAxios.defaults.baseURL = 'https://provisioning.ish.com.au/b';
      defaultAxios.defaults.withCredentials = false;
      defaultAxios.defaults.headers = {
        Authorization: token
      };
      dispatch(getCollegeKey());
      dispatch(getSites());
    }
  }, []);

  const themeHandler = (name: any) => {
    setThemeName(name);
    setTheme(currentTheme(name));
    localStorage.setItem('theme', name);
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
      const storageThemeName = localStorage.getItem('theme');
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
        <Header />
        <Stepper />
        <MessageProvider />
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};

export default Billing;
