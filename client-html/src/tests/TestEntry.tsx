/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { Provider } from "react-redux";
import { StateObservable } from "redux-observable";
import { Subject } from "rxjs";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import TestStore from "../js/constants/Store";
import RootComponent from "../js/RootComponent";
import { initMockDB } from "../dev/mock/MockAdapter";
import { darkTheme, GlobalStylesProvider } from "ish-ui";

// Configuring virtual rendering library and mockedAPI class
export const mockedAPI: any = initMockDB();

export const store = new StateObservable(new Subject(), TestStore.getState());

export const TestEntry = ({ children, state = {} }) => (
  <Provider store={{ ...TestStore, getState: () => ({ ...TestStore.getState(), ...state }) } as any}>
    <RootComponent>
      <ThemeProvider theme={darkTheme}>
        <CssBaseline />
        <GlobalStylesProvider>
          {children}
        </GlobalStylesProvider>
      </ThemeProvider>
    </RootComponent>
  </Provider>
);