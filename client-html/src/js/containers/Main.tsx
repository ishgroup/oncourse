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
import { isDirty } from "redux-form";
import { Route, Switch, withRouter } from "react-router-dom";
import { ThemeProvider } from "@material-ui/core/styles";
import CssBaseline from "@material-ui/core/CssBaseline";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { BrowserWarning } from "../common/components/dialog/BrowserWarning";
import { EnvironmentConstants } from "../constants/EnvironmentConstants";
import store from "../constants/Store";
import { loginRoute, routes } from "../routes";
import MessageProvider from "../common/components/dialog/message/MessageProvider";
import {
 darkTheme, defaultTheme, monochromeTheme, highcontrastTheme, christmasTheme
} from "../common/themes/ishTheme";
import { ThemeContext } from "./ThemeContext";
import {
  APPLICATION_THEME_STORAGE_NAME,
  DASHBOARD_THEME_KEY,
  SYSTEM_USER_ADMINISTRATION_CENTER
} from "../constants/Config";
import {
  DarkThemeKey,
  DefaultThemeKey,
  MonochromeThemeKey,
  HighcontrastThemeKey,
  ChristmasThemeKey,
  ThemeValues
} from "../model/common/Theme";
import { State } from "../reducers/state";
import { AnyArgFunction } from "../model/common/CommonFunctions";
import GlobalStylesProvider from "../common/styles/GlobalStylesProvider";
import { getUserPreferences } from "../common/actions";
import { getGoogleTagManagerParameters } from "../common/components/google-tag-manager/actions";
import { getCurrency, isLoggedIn } from "./preferences/actions";
import ConfirmProvider from "../common/components/dialog/confirm/ConfirmProvider";
import Message from "../common/components/dialog/message/Message";
import SwipeableSidebar from "../common/components/layout/swipeable-sidebar/SwipeableSidebar";

const isAnyFormDirty = (state: State) => {
  const forms = Object.getOwnPropertyNames(state.form);

  if (forms.length) {
    let response = false;

    forms.forEach(n => {
      if (n === "NestedEditViewForm") {
        state.form["NestedEditViewForm"].forEach((n, i) => {
          response = isDirty(`NestedEditViewForm[${i}]`)(state);
        });
      } else {
        response = isDirty(n)(state);
      }
    });

    return response;
  }
  return false;
};

const onWindowClose = e => {
  if (process.env.NODE_ENV !== EnvironmentConstants.production) {
    return;
  }

  const isFormsDirty = isAnyFormDirty(store.getState());

  if (isFormsDirty) {
    e.preventDefault();
    e.returnValue = "All unsaved data will be lost. Are you sure want to close window ?";
  }
};

const RouteContentWrapper = props => {
  const { route: { title }, route } = props;

  useEffect(() => {
    document.title = title;
  }, []);

  return <route.main {...props} routes={route.routes} />;
};

const RouteWithSubRoutes = route => (
  <Route
    path={route.path}
    exact={route.exact}
    render={props => (
      // pass the sub-routes down to keep nesting
      <RouteContentWrapper route={route} {...props} />
    )}
  />
);

const currentTheme = themeName => {
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

interface Props {
  getPreferencesTheme: AnyArgFunction;
  history: any;
  preferencesTheme: ThemeValues;
  onInit: AnyArgFunction;
  isLogged: boolean;
  isLoggedIn: AnyArgFunction;
  match: any;
}

export class MainBase extends React.PureComponent<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      themeName: DefaultThemeKey,
      theme: this.getTheme(defaultTheme),
      showMessage: false,
      successMessage: false,
      messageText: ""
    };
  }

  getTheme = theme => {
    let actualTheme = theme;

    try {
      const storageThemeName = localStorage.getItem(APPLICATION_THEME_STORAGE_NAME);
      if (storageThemeName) {
        actualTheme = currentTheme(storageThemeName);
      }
    } catch (e) {
      console.error(e);
      return actualTheme;
    }

    return actualTheme;
  };

  updateStateFromStorage = () => {
    this.setState({
      themeName: localStorage.getItem(APPLICATION_THEME_STORAGE_NAME)
        ? localStorage.getItem(APPLICATION_THEME_STORAGE_NAME)
        : DefaultThemeKey,
      theme: this.getTheme(defaultTheme)
    });
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
            const url = new RegExp(`\\B${path}|${path}\\B`);
            if (url.test(history.location.pathname)) {
              history.push(route.url);
              pathMatched = true;
            }
          }
        });
      }
    }

    const isLoginFrame = history.location.pathname.match(/login/);

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
    window.addEventListener("beforeunload", onWindowClose, true);
  }

  componentWillUnmount() {
    localStorage.removeItem(APPLICATION_THEME_STORAGE_NAME);
    window.removeEventListener("storage", this.updateStateFromStorage);
    window.removeEventListener("beforeunload", onWindowClose);
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

    localStorage.setItem(APPLICATION_THEME_STORAGE_NAME, name);
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
                routes.map((route, i) => <RouteWithSubRoutes key={i} {...route} />)
              ) : (
                <RouteWithSubRoutes {...loginRoute} />
              )}
            </Switch>
            <ConfirmProvider />
            {isLogged && <SwipeableSidebar />}
          </GlobalStylesProvider>
          <MessageProvider />
        </ThemeProvider>
      </ThemeContext.Provider>
    );
  }
}

const mapStateToProps = (state: State) => ({
  isLogged: state.preferences.isLogged,
  preferencesTheme: state.userPreferences[DASHBOARD_THEME_KEY]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  isLoggedIn: () => dispatch(isLoggedIn()),
  getPreferencesTheme: () => dispatch(getUserPreferences([DASHBOARD_THEME_KEY])),
  onInit: () => {
    dispatch(getGoogleTagManagerParameters());
    dispatch(getCurrency());
    dispatch(getUserPreferences([SYSTEM_USER_ADMINISTRATION_CENTER]));
  }
});

export default withRouter(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MainBase));
