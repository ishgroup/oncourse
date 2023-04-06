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

import React, { useEffect } from "react";
import { Route, Switch, withRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import { connect } from "react-redux";
import { CacheProvider } from '@emotion/react';
import createCache from '@emotion/cache';
import { Dispatch } from "redux";
import { BrowserWarning } from "../common/components/dialog/BrowserWarning";
import { EnvironmentConstants } from "../constants/EnvironmentConstants";
import { loginRoute, routes } from "../routes";
import MessageProvider from "../common/components/dialog/message/MessageProvider";
import { currentTheme, getTheme } from "../common/themes/ishTheme";
import { ThemeContext } from "./ThemeContext";
import {
  APPLICATION_THEME_STORAGE_NAME,
  DASHBOARD_THEME_KEY,
  LICENSE_SCRIPTING_KEY,
  READ_NEWS,
  SYSTEM_USER_ADMINISTRATION_CENTER
} from "../constants/Config";
import { DefaultThemeKey, ThemeValues } from "../model/common/Theme";
import { State } from "../reducers/state";
import { AnyArgFunction } from "../model/common/CommonFunctions";
import GlobalStylesProvider from "../common/styles/GlobalStylesProvider";
import { getUserPreferences } from "../common/actions";
import { getGoogleTagManagerParameters } from "../common/components/google-tag-manager/actions";
import { getCurrency, isLoggedIn } from "./preferences/actions";
import ConfirmProvider from "../common/components/dialog/confirm/ConfirmProvider";
import Message from "../common/components/dialog/message/Message";
import SwipeableSidebar from "../common/components/layout/swipeable-sidebar/SwipeableSidebar";
import { LSGetItem, LSRemoveItem, LSSetItem } from "../common/utils/storage";
import { getDashboardBlogPosts } from "./dashboard/actions";
import { getFormNames, isDirty } from "redux-form";

export const muiCache = createCache({
  key: 'mui',
  prepend: true,
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
  getPreferencesTheme: AnyArgFunction;
  history: any;
  preferencesTheme: ThemeValues;
  onInit: AnyArgFunction;
  isLogged: boolean;
  isAnyFormDirty: boolean;
  isLoggedIn: AnyArgFunction;
  match: any;
}

export class MainBase extends React.PureComponent<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      themeName: DefaultThemeKey,
      theme: getTheme(),
      showMessage: false,
      successMessage: false,
      messageText: ""
    };
  }

  updateStateFromStorage = () => {
    this.setState({
      themeName: LSGetItem(APPLICATION_THEME_STORAGE_NAME)
        ? LSGetItem(APPLICATION_THEME_STORAGE_NAME)
        : DefaultThemeKey,
      theme: getTheme()
    });
  };

  onWindowClose = e => {
    const { isAnyFormDirty } = this.props;

    if (process.env.NODE_ENV !== EnvironmentConstants.production || navigator.webdriver === true) {
      return;
    }

    if (isAnyFormDirty) {
      e.preventDefault();
      e.returnValue = "All unsaved data will be lost. Are you sure want to close window ?";
    }
  };

  UNSAFE_componentWillMount() {
    const {
      isLogged, isLoggedIn, history
    } = this.props;

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

    if (isLogged && !isLoginFrame) {
      this.onInit();
    }
  }

  componentDidMount() {
    const { history } = this.props;

    if (history.location.pathname.match(/Quit/)) {
      window.close();
      return;
    }

    window.addEventListener("storage", this.updateStateFromStorage);
    window.addEventListener("beforeunload", this.onWindowClose, true);
  }

  componentWillUnmount() {
    LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
    window.removeEventListener("storage", this.updateStateFromStorage);
    window.removeEventListener("beforeunload", this.onWindowClose);
  }

  componentDidUpdate(prevProps) {
    const { preferencesTheme, isLogged } = this.props;

    if (!prevProps.isLogged && isLogged) {
      this.onInit();
    }

    if (!prevProps.preferencesTheme && preferencesTheme) {
      this.themeHandler(preferencesTheme);
    }
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo): void {
    console.error(error, errorInfo);

    this.setState({
      showMessage: true,
      successMessage: false,
      messageText: "Something unusual happened in onCourse. Our quality assurance team have been notified."
    });
  }

  onInit = () => {
    const { preferencesTheme, getPreferencesTheme, onInit } = this.props;

    onInit();

    if (!preferencesTheme) {
      getPreferencesTheme();
    }
  };

  themeHandler = (name: ThemeValues) => {
    this.setState({
      themeName: name,
      theme: currentTheme(name)
    });

    LSSetItem(APPLICATION_THEME_STORAGE_NAME, name);
  };

  clearMessage = () => {
    this.setState({
      showMessage: false,
      successMessage: false,
      messageText: ""
    });
  };

  render() {
    const {
     themeName, theme, showMessage, successMessage, messageText
    } = this.state;

    const { isLogged } = this.props;

    return (
      <CacheProvider value={muiCache}>
        <ThemeContext.Provider
          value={{
            themeHandler: this.themeHandler,
            themeName
          }}
        >
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <GlobalStylesProvider>
              <BrowserWarning />
              <Message
                opened={showMessage}
                isSuccess={successMessage}
                text={messageText}
                clearMessage={this.clearMessage}
              />
              <Switch>
                {isLogged ? (
                  routes.map((route, i) => <RouteRenderer key={i} {...route} />)
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
      </CacheProvider>
    );
  }
}

const mapStateToProps = (state: State) => ({
  isLogged: state.preferences.isLogged,
  preferencesTheme: state.userPreferences[DASHBOARD_THEME_KEY],
  isAnyFormDirty: getFormNames()(state).reduce((p,name) => isDirty(name)(state) || p, false)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  isLoggedIn: () => dispatch(isLoggedIn()),
  getPreferencesTheme: () => dispatch(getUserPreferences([DASHBOARD_THEME_KEY])),
  onInit: () => {
    dispatch(getGoogleTagManagerParameters());
    dispatch(getCurrency());
    dispatch(getUserPreferences([SYSTEM_USER_ADMINISTRATION_CENTER, READ_NEWS, LICENSE_SCRIPTING_KEY]));
    dispatch(getDashboardBlogPosts());
  }
});

export default withRouter(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MainBase));