import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {BrowserRouter as Router, Route, Link} from 'react-router-dom';

import {CreateStore} from "./CreateStore";
import Cms from "./containers/Cms";

import "./../scss/main.scss";

const store = CreateStore();


const Blocks = () => (
  <div>Blocks</div>
)

const Pages = () => (
  <div>Pages</div>
)

const routes = [
  {
    path: '/',
    exact: true,
    main: () => <Cms/>,
  },
  {
    path: '/blocks',
    exact: false,
    main: () => <Blocks/>,
  },
  {
    path: '/pages',
    exact: false,
    main: () => <Pages/>,
  },
]


ReactDOM.render(
  <Provider store={store}>
    <Router>
      <div>
        <ul>
          <li><Link to="/">Home</Link></li>
          <li><Link to="/blocks">Blocks</Link></li>
          <li><Link to="/pages">Pages</Link></li>
        </ul>

        <div>
          {routes.map((route, index) => (
            <Route
              key={index}
              path={route.path}
              exact={route.exact}
              component={route.main}
            />
          ))}
        </div>
      </div>
    </Router>
  </Provider>,
  document.getElementById("cms-root"),
);
