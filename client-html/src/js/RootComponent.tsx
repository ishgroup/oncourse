import * as React from "react";
import DateFnsUtils from "@date-io/date-fns";
import history from "./constants/History";
import { Router } from "react-router-dom";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import StylesProviderCustom from "./common/styles/StylesProviderCustom";

const RootComponent = ({ children }) => (
  <StylesProviderCustom>
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Router history={history}>
        <div>{children}</div>
      </Router>
    </MuiPickersUtilsProvider>
  </StylesProviderCustom>
);

export default RootComponent;
