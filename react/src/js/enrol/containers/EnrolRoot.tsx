import * as React from "react";
import {Router, Route, browserHistory, IndexRoute} from "react-router";
import {Payment} from "../components/payment/Payment";
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
