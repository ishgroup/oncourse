import * as React from "react";
import { Route } from "react-router";
import routes from "../routes";

const SecutityAppFrame = () => (
  <>
    {routes
      && routes.map((route, index) => (
        <Route
          exact
          key={index}
          path={route.path}
          render={props => (
            <route.main {...props} />
          )}
        />
      ))}
  </>
);

export default SecutityAppFrame;
