import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {MemoryRouter as Router} from 'react-router-dom';
import {initMockAdapter} from "../../dev/mock/MockAdapter";

import {CreateStore} from "./CreateStore";
import Cms from "./containers/Cms";

import "../scss/main.scss";

const createRootComponent = () => {
  if (document.getElementById('cms-root')) return;

  const rootDiv = document.createElement('div');
  rootDiv.id = 'cms-root';
  rootDiv.className = 'cms';
  document.body.appendChild(rootDiv);
}

// Enable in develop mode (move init to webpack)
initMockAdapter();


createRootComponent();

const store = CreateStore();

ReactDOM.render(
  <Provider store={store}>
    <Router>
      <Cms/>
    </Router>
  </Provider>,
  document.getElementById("cms-root"),
);
