import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {BrowserRouter as Router} from 'react-router-dom';
import {initMockAdapter} from "../../dev/mock/MockAdapter";

import {CreateStore} from "./CreateStore";
import Cms from "./containers/Cms";

import "../scss/main.scss";

// Enable in develop mode (move init to webpack)
initMockAdapter();

const store = CreateStore();

ReactDOM.render(
  <Provider store={store}>
    <Router>
      <Cms/>
    </Router>
  </Provider>,
  document.getElementById("cms-root"),
);
