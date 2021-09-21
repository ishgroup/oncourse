import React, { useEffect, useState } from 'react';
import { ThemeProvider } from '@material-ui/core/styles';
import { useDispatch, useSelector } from 'react-redux';
import Stepper from './stepper/Stepper';
import {
  ChristmasThemeKey,
  DarkThemeKey,
  DefaultThemeKey,
  HighcontrastThemeKey,
  MonochromeThemeKey,
} from '../models/Theme';
import {
  christmasTheme, darkTheme, defaultTheme, highcontrastTheme, monochromeTheme
} from '../themes/ishTheme';
import { ThemeContext } from '../themes/ThemeContext';
import MessageProvider from './common/message/MessageProvider';
import { getCollegeKey, getSites } from '../redux/actions';
import { defaultAxios } from '../api/services/DefaultHttpClient';
import { ExistingCustomerSteps, NewCustomerSteps, Step } from '../models/User';
import { State } from '../redux/reducers';

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

const Billing = () => {
  const [themeName, setThemeName] = useState(DefaultThemeKey);
  const [theme, setTheme] = useState(getTheme(defaultTheme));
  const [steps, setSteps] = useState<Step[]>([]);

  const isNewUser = useSelector<State>((state) => state.isNewUser);

  const dispatch = useDispatch();

  useEffect(() => {
    if (isNewUser) {
      setSteps([...NewCustomerSteps]);
    } else {
      setSteps([...ExistingCustomerSteps]);
      dispatch(getSites());
    }
  }, [isNewUser]);

  useEffect(() => {
    const query = new URLSearchParams(window.location.search);
    const token = query.get('token');
    if (token) {
      defaultAxios.defaults.headers = {
        Authorization: token
      };
      window.history.replaceState(null, null, window.location.pathname);
    }
    dispatch(getCollegeKey());
  }, []);

  const themeHandler = (name: any) => {
    setThemeName(name);
    setTheme(currentTheme(name));
    localStorage.setItem('theme', name);
  };

  return (
    <ThemeContext.Provider
      value={{
        themeHandler,
        themeName
      }}
    >
      <ThemeProvider theme={theme}>
        <Stepper steps={steps} />
        <MessageProvider />
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};

export default Billing;
