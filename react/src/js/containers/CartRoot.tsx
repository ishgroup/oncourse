import * as React from "react";
import {Router, Route, browserHistory, IndexRoute} from 'react-router'
import {Paths} from "../config/Paths";
import {CoursePage} from "./CoursePage";
import {IndexPage} from "./IndexPage";
import Cart from "./Cart";
import {LegacyModal} from "../components/modal/LegacyModal";

export const CartRoot = () => (
  // Will be merged with EnrolRoot in advance
  // (when application become real SPA)
  <div>
    <Cart/>
    <Router history={browserHistory}>
      <Route path="/">
        <Route path={Paths.course} component={CoursePage}/>
        <IndexRoute component={IndexPage}/>
      </Route>
    </Router>
  </div>
);
