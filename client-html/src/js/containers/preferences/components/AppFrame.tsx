import * as React from "react";
import { Route } from "react-router";
import Content from "../../../common/components/layout/Content";
import routes from "../routes";

const AppFrame = () => (
  <>
    {routes &&
      routes.map((route, index) => (
        <Route
          exact
          key={index}
          path={route.path}
          render={props => (
            <Content>
              <route.main {...props} />{" "}
            </Content>
          )}
        />
      ))}
  </>
);

export default AppFrame;
