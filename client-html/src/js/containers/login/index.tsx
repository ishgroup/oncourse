import * as React from "react";
import Loadable from "@react-loadable/revised";
import { MuiThemeProvider } from "@material-ui/core";
import createMuiTheme from "@material-ui/core/styles/createMuiTheme";
import Loading from "../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "login" */ "./LoginPage"),
  loading: Loading
});

const loginPageTheme = theme => createMuiTheme({
  ...theme,
  overrides: {
    MuiInput: {},
  }
});

export default class LoadableLogin extends React.Component {
  render() {
    return (
      <MuiThemeProvider theme={loginPageTheme}>
        <LoadableComponent {...this.props} />
      </MuiThemeProvider>
    );
  }
}
