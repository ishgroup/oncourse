import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {MemoryRouter as Router} from 'react-router-dom';
import {initMockAdapter} from "../../dev/mock/MockAdapter";

import {CreateStore} from "./CreateStore";
import Cms from "./containers/Cms";

const CMS_CSS_PATH = 'http://localhost:8081/assets/main.css';

const createRootComponent = () => {
  const cmsId = "cms-root";

  if (document.getElementById(cmsId)) return;

  const rootDiv = document.createElement('div');
  rootDiv.id = cmsId;
  rootDiv.className = 'cms';
  document.body.appendChild(rootDiv);
};

const loadCmsCss = () => {
  const cmsCssId = "cms-css";
  if (document.getElementById(cmsCssId)) return;

  const head  = document.getElementsByTagName('head')[0];
  const link  = document.createElement('link');
  link.id    = cmsCssId;
  link.rel   = 'stylesheet';
  link.type  = 'text/css';
  link.href  = CMS_CSS_PATH;
  link.media = 'all';
  head.appendChild(link);
};


// Enable in develop mode (move init to webpack)
initMockAdapter();

createRootComponent();
loadCmsCss();

const store = CreateStore();

ReactDOM.render(
  <Provider store={store}>
    <Router>
      <Cms/>
    </Router>
  </Provider>,
  document.getElementById("cms-root"),
);
