import React, { useEffect, useState } from 'react';
import {
  Router,
} from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CacheProvider } from '@emotion/react';
import createCache from '@emotion/cache';
import { Currency } from '@api/model';
import Stepper from './stepper/Stepper';
import { DefaultThemeKey } from '../models/Theme';
import { currentTheme, getTheme } from '../themes/ishTheme';
import { ThemeContext } from '../themes/ThemeContext';
import MessageProvider from './common/message/MessageProvider';
import { defaultAxios } from '../api/services/DefaultHttpClient';
import { useAppDispatch, useAppSelector } from '../redux/hooks/redux';
import { getSites } from '../redux/actions/Sites';
import { getCollegeKey, setCurrency } from '../redux/actions/College';
import Settings from './settings/Settings';
import ConfirmProvider from './common/dialog/ConfirmProvider';
import { getClientId } from '../redux/actions/Google';
import history from '../constant/History';

export const muiCache = createCache({
  key: 'mui',
  prepend: true,
});

const Main = () => {
  const [themeName, setThemeName] = useState(DefaultThemeKey);
  const [theme, setTheme] = useState(getTheme());

  const isNewUser = useAppSelector((state) => state.college.isNewUser);

  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!isNewUser) {
      dispatch(getClientId());
      dispatch(getSites());
    }
  }, [isNewUser]);

  useEffect(() => {
    const query = new URLSearchParams(window.location.search);
    const token = query.get('token') || localStorage.getItem('token');
    const currency = query.get('currency') as Currency;
    if (currency) {
      dispatch(setCurrency(currency));
    }
    if (token) {
      defaultAxios.defaults.headers = {
        Authorization: token
      };
      localStorage.setItem('token', token);
    }
    window.history.replaceState(null, null, window.location.pathname);
    dispatch(getCollegeKey());
  }, []);

  const themeHandler = (name: any) => {
    setThemeName(name);
    setTheme(currentTheme(name));
    localStorage.setItem('theme', name);
  };

  return (
    <CacheProvider value={muiCache}>
      <ThemeContext.Provider
        value={{
          themeHandler,
          themeName
        }}
      >

        <ThemeProvider theme={theme}>
          <Router history={history}>
            {isNewUser ? <Stepper /> : <Settings />}
          </Router>
          <MessageProvider />
          <ConfirmProvider />
        </ThemeProvider>
      </ThemeContext.Provider>
    </CacheProvider>
  );
};

export default Main;
