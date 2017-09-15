import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {MemoryRouter as Router} from 'react-router-dom';
import {initMockAdapter} from "../dev/mock/MockAdapter";

import {CreateStore, RestoreState} from "./CreateStore";
import {configLoader} from "./configLoader";
import Cms from "./containers/Cms";
import {createRootComponent, loadCmsCss} from "./utils";

import {DefaultConfig} from "./constants/Config";

import "../scss/cms.scss";

// Enable in develop mode (move init to webpack)
initMockAdapter();

const store = CreateStore();

/**
 *  Load CMS config
 *  Create cms root element
 *  Load cms styles
**/
configLoader(store);
createRootComponent();
loadCmsCss(store.getState().config.cssPath);

RestoreState(store, () => {
  start(store);
});

const start = store => {
  ReactDOM.render(
    <Provider store={store}>
      <Router>
        <Cms/>
      </Router>
    </Provider>,
    document.getElementById(DefaultConfig.CONTAINER_ID),
  );
}
