import { hot, setConfig } from "react-hot-loader";
import * as React from "react";
import Main from "./containers/Main";
import RootComponent from "./RootComponent";

setConfig({
  logLevel: "debug"
});

const AppEntry = () => (
  <RootComponent>
    <Main />
  </RootComponent>
);

export default hot(module)(AppEntry);
