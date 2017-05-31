import * as React from "react";
import {browserHistory, IndexRoute, Route, Router} from "react-router";
import {Payment} from "./payment/Payment";
import {Checkout} from "./Checkout";
import {Paths} from "../../config/Paths";
import {IndexPage} from "../../components/IndexPage";

export const EnrolRoot = () => (
  <Router history={browserHistory}>
    <Route path="/">
      <Route path={Paths.checkout} component={Checkout}/>
      <Route path={Paths.payment} component={Payment}/>
      <IndexRoute component={IndexPage}/>
    </Route>
  </Router>
);
