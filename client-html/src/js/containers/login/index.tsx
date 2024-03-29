import { createTheme } from '@mui/material';
import { ThemeProvider } from "@mui/material/styles";
import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "login" */ "./LoginPage"),
  loading: Loading
});

const loginPageTheme = theme => createTheme({
  ...theme,
  overrides: {
    MuiInput: {},
  }
});

export default class LoadableLogin extends React.Component {
  render() {
    return (
      <ThemeProvider theme={loginPageTheme}>
        <LoadableComponent {...this.props} />
      </ThemeProvider>
    );
  }
}
