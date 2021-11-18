import * as React from "react";
import { Provider } from "react-redux";
import { configure } from "enzyme";
import Adapter from "@wojtekmaj/enzyme-adapter-react-17";
import { StateObservable } from "redux-observable";
import { Subject } from "rxjs";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import TestStore from "../js/constants/Store";
import RootComponent from "../js/RootComponent";
import { initMockDB } from "../dev/mock/MockAdapter";
import GlobalStylesProvider from "../js/common/styles/GlobalStylesProvider";
import { darkTheme } from "../js/common/themes/ishTheme";

// Configuring virtual rendering library and mockedAPI class

export const mockedAPI: any = initMockDB();

export const store = new StateObservable(new Subject(), TestStore.getState());

configure({ adapter: new Adapter() });

export const TestEntry = ({ children }) => (
  <Provider store={TestStore as any}>
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
