import { Route, Switch } from "react-router-dom";
import * as React from "react";
import routes from "../routes";

const AutomatiomAppFrame = () => (
  <Switch>
    {routes.map((route, index) => (
      <Route exact key={index} path={route.path} render={props => <route.main {...props} />} />
    ))}
  </Switch>
);

export default AutomatiomAppFrame;
