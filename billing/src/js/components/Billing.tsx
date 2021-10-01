import React, { useEffect, useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { useDispatch, useSelector } from 'react-redux';
import { CacheProvider } from '@emotion/react';
import createCache from 'tss-react/@emotion/cache'; // Or "@emotion/cache"
import Stepper from './stepper/Stepper';
import { DefaultThemeKey } from '../models/Theme';
import { currentTheme, getTheme } from '../themes/ishTheme';
import { ThemeContext } from '../themes/ThemeContext';
import MessageProvider from './common/message/MessageProvider';
import { getCollegeKey, getSites } from '../redux/actions';
import { defaultAxios } from '../api/services/DefaultHttpClient';
import { ExistingCustomerSteps, NewCustomerSteps, Step } from '../models/User';
import { State } from '../redux/reducers';

export const muiCache = createCache({
  key: 'mui',
  prepend: true,
});

const Billing = () => {
  const [themeName, setThemeName] = useState(DefaultThemeKey);
  const [theme, setTheme] = useState(getTheme());
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
    <CacheProvider value={muiCache}>
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
    </CacheProvider>
  );
};

export default Billing;
