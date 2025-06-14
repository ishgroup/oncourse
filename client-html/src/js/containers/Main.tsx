/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

/**
 * Main app layout
 * */

import createCache from '@emotion/cache';
import { CacheProvider } from '@emotion/react';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import {
  AnyArgFunction,
  AppTheme,
  BrowserWarning,
  currentTheme,
  DefaultThemeKey,
  getTheme,
  GlobalStylesProvider,
  ThemeValues
} from 'ish-ui';
import React, { useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Route, Switch, withRouter } from 'react-router-dom';
import { Dispatch } from 'redux';
import { getFormNames, isDirty } from 'redux-form';
import { TssCacheProvider } from 'tss-react';
import { getLogo, getUserPreferences, showMessage } from '../common/actions';
import ConfirmProvider from '../common/components/dialog/ConfirmProvider';
import MessageProvider from '../common/components/dialog/MessageProvider';
import { getGoogleTagManagerParameters } from '../common/components/google-tag-manager/actions';
import { ErrorBoundary } from '../common/components/layout/ErrorBoundary';
import SwipeableSidebar from '../common/components/layout/swipeable-sidebar/SwipeableSidebar';
import { LSGetItem, LSRemoveItem, LSSetItem } from '../common/utils/storage';
import {
  APPLICATION_THEME_STORAGE_NAME,
  DASHBOARD_THEME_KEY,
  FORM_NAMES_ALLOWED_FOR_REFRESH,
  LICENSE_SCRIPTING_KEY,
  READ_NEWS,
  SPECIAL_TYPES_DISPLAY_KEY,
  SYSTEM_USER_ADMINISTRATION_CENTER
} from '../constants/Config';
import { EnvironmentConstants } from '../constants/EnvironmentConstants';
import { AppMessage } from '../model/common/Message';
import { State } from '../reducers/state';
import { loginRoute, routes } from '../routes';
import { getDashboardBlogPosts } from './dashboard/actions';
import { getLocation, isLoggedIn } from './preferences/actions';
import { ThemeContext } from './ThemeContext';

export const muiCache = createCache({
  key: 'mui',
  prepend: true,
});

const tssCache = createCache({
  key: "tss"
});

const RouteContentWrapper = props => {
  const { route: { title }, route } = props;

  useEffect(() => {
    document.title = title;
  }, []);

  return <route.main {...props} routes={route.routes} />;
};

const RouteRenderer = route => (
  <Route
    path={route.path}
    exact={route.exact}
    render={props => (
      // pass the sub-routes down to keep nesting
      <RouteContentWrapper route={route} {...props} />
    )}
  />
);

interface Props {
  showMessage: AnyArgFunction<void, AppMessage>;
  getPreferencesTheme: AnyArgFunction;
  history: any;
  preferencesTheme: ThemeValues;
  onInit: AnyArgFunction;
  getLogo: AnyArgFunction;
  isLogged: boolean;
  isAnyFormDirty: boolean;
  isLoggedIn: AnyArgFunction;
  displayAUSReporting: boolean;
}

function MainBase(
  {
    showMessage,
    getPreferencesTheme,
    history,
    preferencesTheme,
    onInit,
    isLogged,
    isAnyFormDirty,
    isLoggedIn,
    displayAUSReporting,
    getLogo
  }: Props) {
    const theme = getTheme();
    
    const [themeName, setThemeName] = useState(DefaultThemeKey);
    const [currentTtheme, setTheme] = useState<AppTheme>({
      ...theme,
      palette: {
        ...theme.palette,
        secondary: {
          ...theme.palette.secondary,
          main: '#434EA1',
        }
      }
    });

  const updateStateFromStorage = () => {
    setThemeName(LSGetItem(APPLICATION_THEME_STORAGE_NAME) as ThemeValues || DefaultThemeKey);
    setTheme(getTheme());
  };

  const onWindowClose = e => {
    if (process.env.NODE_ENV !== EnvironmentConstants.production || navigator.webdriver === true) {
      return;
    }

    if (isAnyFormDirty) {
      e.preventDefault();
      e.returnValue = "All unsaved data will be lost. Are you sure want to close window ?";
    }
  };
  
  useEffect(() => {
    getLogo();
  }, []);
  
  useEffect(() => {
    if (routes) {
      const notFound = routes.find(route => route.url === history.location.pathname);
      if (!notFound) {
        let pathMatched = false;
        routes.forEach(route => {
          if (route.url && route.url !== "/") {
            const path = route.url.replace("/", "");
            // const url = new RegExp(`\\B${path}|${path}\\B`);
            const url = new RegExp(`\\B${path}\\b`);
            if (url.test(history.location.pathname)) {
              history.push(route.url);
              pathMatched = true;
            }
          }
        });
      }
    }

    const isLoginFrame = history.location.pathname.match(/login/) || history.location.pathname.match(/invite/);

    if (!isLogged && !isLoginFrame) {
      isLoggedIn();
    }
  }, [
    history.location.pathname,
    isLogged
  ]);
  
  useEffect(() => {
    window.addEventListener("beforeunload", onWindowClose, true);

    return () => {
      window.removeEventListener("beforeunload", onWindowClose);
    };
  }, [isAnyFormDirty]);

  useEffect(() => {
    window.addEventListener("storage", updateStateFromStorage);

    return () => {
      LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
      window.removeEventListener("storage", updateStateFromStorage);
    };
  }, []);

  useEffect(() => {
    if (isLogged) {
      onInit();
      if (!preferencesTheme) {
        getPreferencesTheme();
      }
    }
  }, [isLogged]);

  const themeHandler = (name: ThemeValues) => {
    setThemeName(name);
    setTheme(currentTheme(name));
    LSSetItem(APPLICATION_THEME_STORAGE_NAME, name);
  };

  useEffect(() => {
    if (preferencesTheme) {
      themeHandler(preferencesTheme);
    }
  }, [preferencesTheme]);
  
  const filteredRoutes = useMemo(() => {
    return routes.filter(r => displayAUSReporting || ![
      "/avetmiss-export",
      "/vetReporting",
    ].includes(r.url));
  }, [displayAUSReporting]);
  
  return (
    <ErrorBoundary showMessage={showMessage}>
      <CacheProvider value={muiCache}>
        <TssCacheProvider value={tssCache}>
          <ThemeContext.Provider
            value={{
              themeHandler,
              themeName
            }}
          >
            <ThemeProvider theme={currentTtheme}>
              <CssBaseline />
              <GlobalStylesProvider>
                <BrowserWarning />
                <Switch>
                  {isLogged ? (
                    filteredRoutes.map((route, i) => <RouteRenderer key={i} {...route} />)
                  ) : (
                    loginRoute.map((route, i) => <RouteRenderer key={i} {...route} />)
                  )}
                </Switch>
                <ConfirmProvider />
                {isLogged && <SwipeableSidebar />}
              </GlobalStylesProvider>
              <MessageProvider />
            </ThemeProvider>
          </ThemeContext.Provider>
        </TssCacheProvider>
      </CacheProvider>
    </ErrorBoundary>
  );
}

const mapStateToProps = (state: State) => ({
  isLogged: state.preferences.isLogged,
  preferencesTheme: state.userPreferences[DASHBOARD_THEME_KEY],
  displayAUSReporting: state.location.countryCode === 'AU',
  isAnyFormDirty: getFormNames()(state).filter(name => !FORM_NAMES_ALLOWED_FOR_REFRESH.includes(name)).reduce((p, name) => isDirty(name)(state) || p, false)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  showMessage: message => dispatch(showMessage(message)),
  isLoggedIn: () => dispatch(isLoggedIn()),
  getPreferencesTheme: () => dispatch(getUserPreferences([DASHBOARD_THEME_KEY])),
  getLogo: () => dispatch(getLogo()),
  onInit: () => {
    dispatch(getLocation());
    dispatch(getGoogleTagManagerParameters());
    dispatch(getUserPreferences([SYSTEM_USER_ADMINISTRATION_CENTER, READ_NEWS, LICENSE_SCRIPTING_KEY, SPECIAL_TYPES_DISPLAY_KEY]));
    dispatch(getDashboardBlogPosts());
  }
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(MainBase));