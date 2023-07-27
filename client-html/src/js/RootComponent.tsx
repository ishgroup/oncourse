/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { Router } from "react-router-dom";
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import history from "./constants/History";
import "./constants/Prototype";
import { StylesProviderCustom } from "ish-ui";

const RootComponent = ({ children }) => (
  <StylesProviderCustom>
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Router history={history}>
        {children}
      </Router>
    </LocalizationProvider>
  </StylesProviderCustom>
);

export default RootComponent;