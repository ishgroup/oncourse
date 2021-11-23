import * as React from "react";
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import { Router } from "react-router-dom";
import LocalizationProvider from '@mui/lab/LocalizationProvider';
import StylesProviderCustom from "./common/styles/StylesProviderCustom";
import history from "./constants/History";

const RootComponent = ({ children }) => (
  <StylesProviderCustom>
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Router history={history}>
        <div>{children}</div>
      </Router>
    </LocalizationProvider>
  </StylesProviderCustom>
);

export default RootComponent;
